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

import com.github.besherman.lifx.impl.entities.internal.LFXDeviceID;
import com.github.besherman.lifx.impl.entities.internal.LFXMessage;
import java.util.Set;

/**
 * This is the main interface for handling and keeping track of lights. The
 * message router takes care of routing messages - the implementation of 
 * this interface does the rest.
 */
public interface LFXLightHandler {
    /**
     * Sets a new router. This will always be called before open().
     * @param router a new router or null (which is passed before closing)
     */
    void setRouter(LFXMessageRouter router);
    
    /**
     * Handles a message from the network.
     * 
     * @param targets the recipients of the message, can be empty
     * @param message the received message
     */
    void handleMessage(Set<LFXDeviceID> targets, LFXMessage message);    
    
    /**
     * Opens the handler. This will always be called after setRouter() and 
     * before handleMessage.
     */
    void open();
    
    /**
     * Closes the handler. After this the handler will not receive any more
     * messages. Note that after this the router will be set to null. The handler
     * is not allowed to keep an intance of the router after the handler has
     * been closed.
     */
    void close();
}
