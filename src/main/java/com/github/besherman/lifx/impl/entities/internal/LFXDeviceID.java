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
package com.github.besherman.lifx.impl.entities.internal;

import java.util.Arrays;

/**
 *
 * @author Richard
 */
public class LFXDeviceID {
    private static final int DEVICE_TARGET_ID_BYTES = 6;
    private final byte[] deviceBytes;
    private final String stringRepresentation;

    public LFXDeviceID(byte[] bytes) {
        if(bytes == null) {
            throw new IllegalArgumentException("bytes can not be null");            
        }
        this.stringRepresentation = LFXByteUtils.byteArrayToHexString(bytes);
        if(bytes.length != DEVICE_TARGET_ID_BYTES) {
            throw new IllegalArgumentException("bytes expected to be " + 
                    DEVICE_TARGET_ID_BYTES + " was " + bytes.length + ": " + stringRepresentation);
        }
        
        deviceBytes = Arrays.copyOf(bytes, DEVICE_TARGET_ID_BYTES);
    }
    
    public LFXDeviceID(String hexString) {
        this.deviceBytes = LFXByteUtils.hexStringToByteArray(hexString);
        this.stringRepresentation = hexString;
    }
    
    public byte[] getDeviceDataValue() {
        return Arrays.copyOf(deviceBytes, DEVICE_TARGET_ID_BYTES);
    }
    
    public String getStringRepresentation() {
        return stringRepresentation;
    }

    @Override
    public String toString() {
        return "LFXDeviceID{" + stringRepresentation + '}';
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Arrays.hashCode(this.deviceBytes);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LFXDeviceID other = (LFXDeviceID) obj;
        if (!Arrays.equals(this.deviceBytes, other.deviceBytes)) {
            return false;
        }
        return true;
    }
}
