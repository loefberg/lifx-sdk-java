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

import java.util.ArrayList;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Specifies the target of a message. This can be one of three things:
 * 
 * <ul>
 * <li>LFXBinaryTargetType.BROADCAST: Targets all bulbs. 
 *                                    {@link #getStringValue()} will return "*".</li>
 * <li>LFXBinaryTargetType.TAG:       Targets all bulbs with with the given tags. 
 *                                    {@link #getStringValue()} will return 
 *                                    "#&lt;bit field as hex string&gt;"</li>
 * <li>LFXBinaryTargetType.DEVICE:    Targets a single bulb with the given 
 *                                    MAC address. {@link #getStringValue()} will return 
 *                                    "&lt;the max address as hex string&gt;"</li>
 * </ul>
 * 
 * This is part of {@link LFXBinaryPath}.
 */
public class LFXBinaryTargetID {
    // Canonical representation is a string
    //
    // Device: 6 byte, 12 char hex string				(704192abcd12)
    // Group: uint64 bitfield prefixed with # in hex	(#8 - group 4)
    // Broadcast: "*"

    private static final int DEVICE_TARGET_ID_BYTES = 6;


    private final LFXBinaryTargetType targetType;
    private final Set<LFXTagID> groupTagField = EnumSet.noneOf(LFXTagID.class);
    private final LFXDeviceID deviceId;

    public LFXBinaryTargetID() {
        targetType = LFXBinaryTargetType.BROADCAST;
        deviceId = null;
    }
    
    public LFXBinaryTargetID(LFXDeviceID deviceId) {
        this.targetType = LFXBinaryTargetType.DEVICE;
        this.deviceId = deviceId;
    }    
    
    public LFXBinaryTargetID(Set<LFXTagID> tagField) {        
        targetType = tagField.isEmpty() ? LFXBinaryTargetType.BROADCAST : LFXBinaryTargetType.TAG;
        groupTagField.addAll(tagField);        
        deviceId = null;        
    }    
    
    public LFXDeviceID getDeviceID() {
        if(targetType != LFXBinaryTargetType.DEVICE) {
            throw new IllegalStateException("target is not DEVICE it is " + targetType);
        }
        return deviceId;
    }

    public LFXBinaryTargetType getTargetType() {
        return targetType;
    }

//    public static LFXBinaryTargetID getTargetIDWithString(String stringValue) {
//        if (stringValue.contains("*")) {
//            return new LFXBinaryTargetID();
//        }
//
//        if (stringValue.contains("#")) {
//            String hexString = stringValue.substring(stringValue.indexOf('#') + 1);
////            TagField tagField = new TagField();
////            tagField.tagData = LFXByteUtils.hexStringToByteArray(hexString);
//            Set<LFXTagID> tagField = LFXTagID.unpack(UInt64.fromHex(hexString));
//            return new LFXBinaryTargetID(tagField);
//        } else {
//            // Device Target (6 bytes)
//            //return getDeviceTargetIDWithString(stringValue);
//            return new LFXBinaryTargetID(stringValue);
//        }
//    }



    /**
     * Returns this target as a string. 
     * <ul>
     * <li>LFXBinaryTargetType.BROADCAST: "*".</li>
     * <li>LFXBinaryTargetType.TAG:       "#&lt;bit field as hex string&gt;"</li>
     * <li>LFXBinaryTargetType.DEVICE:    "&lt;the max address as hex string&gt;"</li>
     * </ul>
     */
    public String getStringValue() {
        switch (targetType) {
            case BROADCAST: {
                return "*";
            }
            case TAG: {
                return "#" + LFXTagID.pack(groupTagField).toHex();
            }
            case DEVICE: {
                return deviceId.toString();	
            }
        }

        return "LFXBinaryTarget: Unknown Type";
    }

    @Override
    public String toString() {
        return "LFXBinaryTargetID{" + getStringValue() + '}';
    }

    public LFXBinaryTargetType geTargetType() {
        return targetType;
    }

    public Set<LFXTagID> getGroupTagField() {
        return groupTagField;
    }

    public List<LFXBinaryTargetID> getIndividualGroupTargetIDs() {
        // For future optimisation, this could get generated once, when a groupTargetID is created
        List<LFXBinaryTargetID> targetIDs = new ArrayList<>();

        for(LFXTagID tag: groupTagField) {
            targetIDs.add(new LFXBinaryTargetID(EnumSet.of(tag)));
        }
        
        return targetIDs;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.targetType);
        hash = 43 * hash + Objects.hashCode(this.groupTagField);
        hash = 43 * hash + Objects.hashCode(this.deviceId);
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
        final LFXBinaryTargetID other = (LFXBinaryTargetID) obj;
        if (this.targetType != other.targetType) {
            return false;
        }
        if (!Objects.equals(this.groupTagField, other.groupTagField)) {
            return false;
        }
        if (!Objects.equals(this.deviceId, other.deviceId)) {
            return false;
        }
        return true;
    }



}
