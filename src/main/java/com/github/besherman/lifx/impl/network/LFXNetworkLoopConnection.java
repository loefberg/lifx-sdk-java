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

import com.github.besherman.lifx.impl.entities.internal.LFXByteUtils;
import com.github.besherman.lifx.impl.entities.internal.LFXMessage;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
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
 *    <li>Reading messages from the outgoing queue and writing them to the network.</li> 
 * </ol>
 */
public class LFXNetworkLoopConnection {
    private static final int PORT = 56700;    
    private final int messageSendRateLimitInterval;        
    
    private final LFXMessageRouter router;            
    private final BlockingQueue<LFXSocketMessage> outgoingQueue;     
        
    private Reader reader;    
    private Thread readingThread;
    
    private Writer writer;
    private Thread writingThread;        
    
    public LFXNetworkLoopConnection(String broadcastAddress, LFXLightHandlerModel handlers) {
        this.messageSendRateLimitInterval = LFXConstants.getNetworkLoopSendRateLimitInterval();        
        int outgoingQueueSize = LFXConstants.getOutgoingQueueSize();
        
        this.outgoingQueue = new PriorityBlockingQueue<>(outgoingQueueSize);
        this.router = new LFXMessageRouter(broadcastAddress, handlers, outgoingQueue);
    }
    
    
    public void open() throws IOException {
        try {
            writer = new Writer(outgoingQueue, messageSendRateLimitInterval);
            writingThread = new Thread(writer, "LIFX Network Writer");
            writingThread.start();

            reader = new Reader(writer.getChannel(), router);
            readingThread = new Thread(reader, "LIFX Network Reader");        
            readingThread.start();                     
        } catch(IOException ex) {
            close();                    
            throw ex;
        }
    }
        
    public void close() {
        if(reader != null) {
            reader.close();
        }

        if(writer != null) {
            writer.close();                
        }

        try {
            if(readingThread != null) {
                readingThread.join();
            }

            if(writingThread != null) {
                writingThread.join();
            }
        } catch(InterruptedException ex) {
            // TODO: maybee we should interrupt the writing thread here?
        }
    }    
    
    private static class Writer implements Runnable {
        private static final int BUF_SIZE = 255;
        private final DatagramChannel channel;
        private final BlockingQueue<LFXSocketMessage> outgoingQueue;
        private final AtomicBoolean running = new AtomicBoolean(true);    
        private final int messageSendRateLimitInterval;

        public Writer(BlockingQueue<LFXSocketMessage> outgoingQueue, int messageSendRateLimitInterval) throws IOException {
            this.channel = DatagramChannel.open();
            this.channel.socket().setBroadcast(true);
            this.outgoingQueue = outgoingQueue;
            this.messageSendRateLimitInterval = messageSendRateLimitInterval;
        }
        
        public DatagramChannel getChannel() {
            return channel;
        }
        
        public void close() {
            running.set(false);
        }

        @Override
        public void run() {
            ByteBuffer buf = ByteBuffer.allocate(BUF_SIZE);
            try {
                // we don't want to stop before the queue is empty because
                // then the stuff we asked for wont happen and the user
                // will be confused - we've hopefully stopped the reader though
                // so nothing new will end up on the queue                
                while(running.get() || !outgoingQueue.isEmpty()) {
                    //
                    // Send outgoing messages
                    //                    
                    LFXSocketMessage msg = outgoingQueue.poll(1, TimeUnit.SECONDS);                    
                    if(msg != null) {
                        buf.clear();
                        buf.put(msg.getMessageData());
                        buf.flip();

                        try {
                            channel.send(buf, msg.getAddress());
                        } catch(Exception ex) {
                            Logger.getLogger(LFXNetworkLoopConnection.class.getName()).log(Level.SEVERE, 
                                    "Failed to send message", ex);
                        }

                        Thread.sleep(messageSendRateLimitInterval);                                            
                    }                    
                }                
            } catch(Exception ex) {
                Logger.getLogger(Writer.class.getName()).log(Level.SEVERE, "Writer died unexpectadly");
            } finally {
                try {
                    channel.close();
                } catch(IOException ex) {
                    Logger.getLogger(Writer.class.getName()).log(Level.SEVERE, 
                            "Failed to close DatagramChannel", ex);
                }
            }
        }      
                
    }
    
    private static class Reader implements Runnable {
        private static final int BUF_SIZE = 255;

        private final AtomicBoolean running = new AtomicBoolean(true);    

        private final Selector selector;
        private final LFXMessageRouter router;  

        public Reader(DatagramChannel channel, LFXMessageRouter router) throws IOException {            
            this.router = router;
            channel.configureBlocking(false);
            channel.socket().setReuseAddress(true);
            channel.socket().bind(new InetSocketAddress(PORT));

            selector = Selector.open();
            channel.register(selector, SelectionKey.OP_READ);                    
        }

        public void close() {                        
            running.set(false);
            selector.wakeup();
        }

        @Override
        public void run() {
            Logger.getLogger(LFXNetworkLoopConnection.class.getName()).log(Level.FINE, "Starting event loop");        
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
                        Logger.getLogger(LFXNetworkLoopConnection.class.getName()).log(Level.SEVERE, 
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
                                    Logger.getLogger(LFXNetworkLoopConnection.class.getName()).log(Level.SEVERE, 
                                            "Failed to receive message", ex);
                                }   

                                // source is null when receive got nothing
                                if(source != null) {
                                    buf.flip();                                
                                    byte[] bytes = new byte[buf.remaining()];
                                    buf.get(bytes);

                                    // sometimes we get an empty package for some reason
                                    if(bytes.length > 0) { 
                                        String messageAsHex = LFXByteUtils.byteArrayToHexString(bytes);                                        
                                        
                                        LFXMessage msg = null;
                                        try {
                                            msg = new LFXMessage(bytes);
                                        } catch(Exception ex) {
                                            Logger.getLogger(LFXNetworkLoopConnection.class.getName()).log(Level.SEVERE, 
                                                    "Failed to parse message: " + messageAsHex, ex);
                                        }                                    

                                        if(msg != null) {                                            
                                            Logger.getLogger(Reader.class.getName()).log(Level.FINEST, "Received message {0}", msg.getType());
                                            try {                                    
                                                InetAddress addr = ((InetSocketAddress)source).getAddress();
                                                router.handleMessage(msg.withSource(addr));
                                            } catch(Exception ex) {
                                                Logger.getLogger(LFXNetworkLoopConnection.class.getName()).log(Level.SEVERE, 
                                                        "Failed to handle message: " + messageAsHex, ex);
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
                Logger.getLogger(LFXNetworkLoopConnection.class.getName()).log(Level.SEVERE, 
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
            }
            
            Logger.getLogger(LFXNetworkLoopConnection.class.getName()).log(Level.FINE, 
                    "Stopping event loop");
        }

    }
}
    
