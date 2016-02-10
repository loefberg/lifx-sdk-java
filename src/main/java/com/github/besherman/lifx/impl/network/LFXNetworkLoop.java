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

import java.io.IOException;

/**
 * 
 */
public class LFXNetworkLoop {
    private static final int PORT = 56700;    
    private static final Object instanceLock = new Object();
    private static LFXNetworkLoop instance;
    
    private final LFXLightHandlerModel handlers = new LFXLightHandlerModel();
    
    private LFXNetworkLoopConnection connection;
    private final Object conLock = new Object();
    private String broadcastAddress = null;
    
    
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
        synchronized(conLock) {
            return connection != null;
        }
    }
    
    public void open() throws IOException {
        synchronized(conLock) {
            if(connection == null) {
                LFXNetworkLoopConnection newConnection = new LFXNetworkLoopConnection(broadcastAddress, handlers);
                newConnection.open();
                connection = newConnection;
            }
        }
    }
        
    public void close() {
        synchronized(conLock) {
            if(connection != null) {
                connection.close();
                connection = null;
            }
        }
    }    

    public void setBroadcastAddress(String broadcastAddress) {
        this.broadcastAddress = broadcastAddress;
    }
    
    public void addHandler(LFXLightHandler handler) {
        handlers.addLightHandler(handler);
    }
    
    public void removeHandler(LFXLightHandler handler) {
        handlers.removeLightHandler(handler);
    }    
}
    
