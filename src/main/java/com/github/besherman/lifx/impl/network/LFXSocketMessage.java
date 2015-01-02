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

import java.net.InetSocketAddress;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

/**
 *
 */
public class LFXSocketMessage implements Comparable<LFXSocketMessage> {
    public static final int LOW_PRIORITY = 100;
    public static final int HIGH_PRIORITY = 10;
    
    
    private final long timestamp;
    private final int priority;
    private final byte[] bytes;
    private final InetSocketAddress address;

    public LFXSocketMessage(byte[] bytes, InetSocketAddress address, int priority) {
        this.bytes = bytes;
        this.address = address;
        this.priority = priority;
        this.timestamp = System.nanoTime();
    }
    
    public byte[] getMessageData() {
        return bytes;
    }
    
    public InetSocketAddress getAddress() {
        return address;
    }

    /**
     * Order by priority first then timestamp;
     */
    @Override
    public int compareTo(LFXSocketMessage other) {
        int comparePrio = Integer.compare(this.priority, other.priority);
        return comparePrio != 0 
                ? comparePrio
                : Long.compare(this.timestamp, other.timestamp);
    }
}
