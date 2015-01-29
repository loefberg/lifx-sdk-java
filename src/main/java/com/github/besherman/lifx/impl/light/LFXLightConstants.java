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
package com.github.besherman.lifx.impl.light;

/**
 * These are settings specific to lights and groups. For light related to
 * network settings see LFXConstants.
 */
public class LFXLightConstants {
    /**
     * Number of milliseconds we wait until a light is removed.
     * 
     * When a light loses power we never get to know that is is gone, so we 
     * remember when we last got a message from a light. When we haven't seen
     * a light for long enough we say that it is lost.
     */
    public static int getLightLostTimeout() {
        return Integer.parseInt(System.getProperty("com.github.besherman.lifx.light.lightLostTimeout", "35000"));
    }
    
}
