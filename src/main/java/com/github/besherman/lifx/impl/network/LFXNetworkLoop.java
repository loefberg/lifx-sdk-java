/*
 * The MIT License
 *
 * Copyright 2014 Richard Löfberg.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.besherman.lifx.impl.network;

/**
 *
 * @author Richard
 */

import com.github.besherman.lifx.impl.entities.internal.LFXMessage;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The network loop handles reading and writing from the network. It is is 
 * responsible for:
 * <ol>
 *    <li>Reading messages from the network and passing them to the {@link LFXMessageRouter}.</li>
 *    <li>Reading messages from the {@link OutgoingQueue} and writing them to the network.</li> 
 *    <li>Passing references of {@link LFXLightHandler} to {@link LFXMessageRouter}. See {@link #addHandler(LightHandler)}</li> 
 * </ol>
 */
public class LFXNetworkLoop {
    private static final int PORT = 56700;    
    private static final Object instanceLock = new Object();
    private static LFXNetworkLoop instance;
    
    private final BlockingQueue<LFXSocketMessage> outgoingQueue = new ArrayBlockingQueue<>(100); // TODO: this should be configurable
    private final LFXMessageRouter router = new LFXMessageRouter(outgoingQueue);        
    private final Object openLock = new Object();
    private final AtomicBoolean opened = new AtomicBoolean(false);
    
    private Reader reader;    
    private Thread readingThread;
    
    private Writer writer;
    private Thread writingThread;
    
    private LFXNetworkLoop() {        
    }
    
    public static LFXNetworkLoop getLoop() {
        synchronized(instanceLock) {
            if(instance == null) {
                instance = new LFXNetworkLoop();                
            }
            return instance;
        }
    }
    
    public boolean isOpen() {
        return opened.get();
    }
    
    public void open() throws IOException {
        synchronized(openLock) {
            if(!opened.getAndSet(true)) {
                DatagramChannel channel = DatagramChannel.open();
                reader = new Reader(channel, router);
                readingThread = new Thread(reader, "LIFX Network Reader");        
                readingThread.start();     
                
                writer = new Writer(channel, outgoingQueue);
                writingThread = new Thread(writer, "LIFX Network Writer");
                writingThread.start();                
            } else {
                Logger.getLogger(LFXNetworkLoop.class.getName()).log(Level.INFO, "NetworkLoop already opened");
            }
        }
    }
        
    public void close() {
        synchronized(openLock) {
            if(opened.getAndSet(false)) {
                reader.close();
                writer.close();
                writingThread.interrupt();

                try {
                    readingThread.join();
                    writingThread.join();
                } catch(InterruptedException ex) {}
            } else {
                Logger.getLogger(LFXNetworkLoop.class.getName()).log(Level.INFO, "NetworkLoop already closed");
            }
        }
    }    
    
    public void addHandler(LFXLightHandler handler) {
        router.addHandler(handler);
    }
    
    public void removeHandler(LFXLightHandler handler) {
        router.removeHandler(handler);
    }
    
    private static class Writer implements Runnable {
        private static final int BUF_SIZE = 255;
        private final DatagramChannel sendingChannel;
        private final BlockingQueue<LFXSocketMessage> outgoingQueue;
        private final AtomicBoolean running = new AtomicBoolean(true);    

        public Writer(DatagramChannel sendingChannel, BlockingQueue<LFXSocketMessage> outgoingQueue) {
            this.sendingChannel = sendingChannel;
            this.outgoingQueue = outgoingQueue;
        }
        
        public void close() {
            running.set(false);
        }

        @Override
        public void run() {
            ByteBuffer buf = ByteBuffer.allocate(BUF_SIZE);
            try {
                while(running.get()) {
                    //
                    // Send outgoing messages
                    //                    
                    LFXSocketMessage msg = outgoingQueue.poll(1, TimeUnit.SECONDS);                    
                    if(msg != null) {
                        buf.clear();
                        buf.put(msg.getMessageData());
                        buf.flip();

                        try {
                            sendingChannel.send(buf, msg.getAddress());
                        } catch(Exception ex) {
                            Logger.getLogger(LFXNetworkLoop.class.getName()).log(Level.SEVERE, 
                                    "Failed to send message", ex);
                        }

                        // TODO: this should be configurable
                        // lifx-sdk-android has this set to 200
                        Thread.sleep(50);                                            
                    }                    
                }                
            } catch(InterruptedException ex) {
                // expected
            } catch(Exception ex) {
                Logger.getLogger(Writer.class.getName()).log(Level.SEVERE, "Writer died unexpectadly");
            }
        }      
                
    }
    
    private static class Reader implements Runnable {
        private static final int BUF_SIZE = 255;

        private final AtomicBoolean running = new AtomicBoolean(true);    

        private final Selector selector;
        private final LFXMessageRouter router;  
        private final DatagramChannel sendingChannel;

        public Reader(DatagramChannel channel, LFXMessageRouter router) throws IOException {            
            this.router = router;
            channel.configureBlocking(false);
            channel.socket().bind(new InetSocketAddress(PORT));

            selector = Selector.open();
            channel.register(selector, SelectionKey.OP_READ);                    

            sendingChannel = channel;
        }

        public void close() {                        
            running.set(false);
            selector.wakeup();
        }

        @Override
        public void run() {
            Logger.getLogger(LFXNetworkLoop.class.getName()).log(Level.FINE, "Starting event loop");        
            try {
                router.open();

                ByteBuffer buf = ByteBuffer.allocate(BUF_SIZE);
                while(running.get()) {
                    //
                    // Check for new messages
                    //
                    int selected = 0;            
                    try {
                        selected = selector.select();
                    } catch(IOException ex) {
                        Logger.getLogger(LFXNetworkLoop.class.getName()).log(Level.SEVERE, 
                                "Failed to select channel", ex);
                    } 

                    if(selected > 0) {
                        Set<SelectionKey> keys = selector.selectedKeys();
                        for(SelectionKey key: keys) {
                            if(key.isReadable()) {                            
                                buf.clear();
                                DatagramChannel ch = (DatagramChannel)key.channel();                        
                                SocketAddress source = null;

                                try {
                                    source = ch.receive(buf);
                                } catch(IOException ex) {
                                    Logger.getLogger(LFXNetworkLoop.class.getName()).log(Level.SEVERE, 
                                            "Failed to receive message", ex);
                                }   

                                // source is null when receive got nothing
                                if(source != null) {
                                    buf.flip();                                
                                    byte[] bytes = new byte[buf.remaining()];
                                    buf.get(bytes);

                                    // sometimes we get an empty package for some reason
                                    if(bytes.length > 0) { 
                                        LFXMessage msg = null;
                                        try {
                                            msg = new LFXMessage(bytes);
                                        } catch(Exception ex) {
                                            // TODO: print the byte array
                                            Logger.getLogger(LFXNetworkLoop.class.getName()).log(Level.SEVERE, 
                                                    "Failed to parse message", ex);
                                        }
                                    

                                        if(msg != null) {
                                            try {                                    
                                                InetAddress addr = ((InetSocketAddress)source).getAddress();
                                                router.handleMessage(msg.withSource(addr));
                                            } catch(Exception ex) {
                                                Logger.getLogger(LFXNetworkLoop.class.getName()).log(Level.SEVERE, 
                                                        "Failed to handle message", ex);
                                            }
                                        }
                                    }
                                } 
                            }                        
                        }
                        keys.clear();
                    }                    
                }

            } catch(Exception ex) {
                // not supposed to end up there
                Logger.getLogger(LFXNetworkLoop.class.getName()).log(Level.SEVERE, 
                        "Event Loop unexpectedly died", ex);
            } finally {
                try {
                    router.close();
                } catch(Exception ex) {
                    Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, 
                            "Failed to close router", ex);
                }
                
                try {
                    selector.close();
                } catch(IOException ex) {
                    Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, 
                            "Failed to close selector", ex);
                }
                
                try {
                    sendingChannel.close();
                } catch(IOException ex) {
                    Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, 
                            "Failed to close channel", ex);
                }
            }
            
            Logger.getLogger(LFXNetworkLoop.class.getName()).log(Level.FINE, 
                    "Stopping event loop");
        }

    }
}
    
