/*
 * The MIT License
 *
 * Copyright 2015 Richard.
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
 * These are settings specific to the network related code. For light related
 * settings see LFXLightConstants.
 */
public class LFXConstants {    
    /**
     * Returns the wait time between sending messages. Flooding the network
     * will just cause the bulbs to get confused.
     * 
     * lifx-sdk-android sets this to 200
     */
    public static int getNetworkLoopSendRateLimitInterval() {
        return Integer.parseInt(System.getProperty(
                "com.github.besherman.lifx.messageSendRateLimitInterval", "20"));        
    }
    
    /**
     * Messages that are to be sent to the network are added to a queue. This
     * is the max size of that queue.
     * 
     * An unbounded queue is dangerous because it will hide problems. So this
     * value should be high but not to high that we never knows that there is
     * a problem.
     */
    public static int getOutgoingQueueSize() {
        return Integer.parseInt(System.getProperty(
                "com.github.besherman.lifx.outgoingQueueSize", "500"));        
    }

    /**
     * How long the response tracker waits until it resends a message.     
     */
    public static int getResponseTrackerResendTimeout() {
        return Integer.parseInt(System.getProperty(
                "com.github.besherman.lifx.responseTrackerTimeout", "500"));        
    }
    
    /**
     * The response tracker has a single timer that wakes it up and checks for
     * timeouts. This is how long it sleeps.
     */
    public static int getResponseTrackerInterval() {
        return Integer.parseInt(System.getProperty(
                "com.github.besherman.lifx.responseTrackerInterval", "100"));
    }
}
