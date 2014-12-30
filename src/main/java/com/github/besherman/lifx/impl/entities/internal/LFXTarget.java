/*
 * The MIT License
 *
 * Created by Jarrod Boyes on 24/03/14.
 * Copyright (c) 2014 LIFX Labs. All rights reserved.
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

import java.util.Objects;

/**
 * Specifies the target of a message, used for outgoing messages. 
 * When a message is sent through a gateway, the routing table will be used 
 * to create {@link LFXBinaryPath}s from this.
 */
public class LFXTarget {
    private static final LFXTarget broadcast = new LFXTarget();
    
    public static LFXTarget getBroadcastTarget() {
        return broadcast;
    }
    
    
    private final LFXTargetType targetType;
    private final LFXDeviceID deviceID;
    private final LFXTagID tag;

    /**
     * Creates a broadcast target. Used to send messages to all bulbs. 
     * See {@link #getBroadcastTarget()}.
     */
    private LFXTarget() {
        this.targetType = LFXTargetType.BROADCAST;
        this.deviceID = null;
        this.tag = null;
    }
    
    /**
     * Creates a bulb target. Used to send messages to a specific bulb.
     */
    public LFXTarget(LFXDeviceID deviceId) {
        this.targetType = LFXTargetType.DEVICE;
        this.deviceID = deviceId;
        this.tag = null;
    }

    /**
     * Creates a tag target. Used to send messages to all bulbs with the given
     * tag.
     */
    public LFXTarget(LFXTagID tag) {
        this.targetType = LFXTargetType.TAG;
        this.deviceID = null;
        this.tag = tag;
    }
    
    public LFXTargetType getTargetType() {
        return targetType;
    }

    public LFXTagID getTag() {
        return tag;
    }

    public LFXDeviceID getDeviceID() {
        return deviceID;
    }
    

    @Override
    public String toString() {
        return "LFXTarget{" + "targetType=" + targetType + ", deviceID=" + deviceID + ", tag=" + tag + '}';
    }
    
    @Override
    public boolean equals(Object other) {
        if(other instanceof LFXTarget == false) {
            return false;
        }
        
        LFXTarget aTarget = (LFXTarget)other;

        if (this.targetType != aTarget.targetType) {
            return false;
        }

        switch (targetType) {
            case BROADCAST:
                return true;
            case DEVICE:
                return deviceID.equals(aTarget.deviceID);
            case TAG:
                return tag.equals(aTarget.tag);
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.targetType);
        hash = 23 * hash + Objects.hashCode(this.deviceID);
        hash = 23 * hash + Objects.hashCode(this.tag);
        return hash;
    }    
}
