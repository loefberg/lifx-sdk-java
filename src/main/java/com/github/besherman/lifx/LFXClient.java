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
package com.github.besherman.lifx;

import com.github.besherman.lifx.impl.light.LFXDefaultLightHandler;
import com.github.besherman.lifx.impl.network.LFXNetworkLoop;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 
 */
public class LFXClient {
    private final LFXDefaultLightHandler lightHandler = new LFXDefaultLightHandler();
    private final LFXNetworkLoop loop;
   
    /**
     * Creates a new client.
     */
    public LFXClient() {
        loop = LFXNetworkLoop.getLoop();
        loop.addHandler(lightHandler);
    }

    /**
     * Creates a new client.
     */
    public LFXClient(String broadcastAddress) {
				this();
				loop.setBroadcastAddress(broadcastAddress);
    }
    
    /**
     * Returns the collection of lights.
     */
    public LFXLightCollection getLights() {
        return lightHandler.getLights();
    }

    /**
     * Returns the collection of groups.
     */
    public LFXGroupCollection getGroups() {
        return lightHandler.getGroups();
    }

    /**
     * Returns true if the client is opened.
     */
    public boolean isOpen() {
        return loop.isOpen();
    }

    /**
     * Opens the client for reading/writing on the network.
     * @param block if true the call will block until the lights has been loaded
     * @throws IOException if the client fails to connect to the network.
     */
    public void open(boolean block) throws IOException, InterruptedException {
        loop.open();
        
        if(block) {
            lightHandler.waitForLoaded(300, TimeUnit.SECONDS);
        }
    }
    
    /**
     * Closes the client and stops all threads.
     */
    public void close() {
        loop.close();
    }
}
