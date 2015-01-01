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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol;
import com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type;
import com.github.besherman.lifx.impl.entities.internal.structle.StructleTypes;
import com.github.besherman.lifx.impl.entities.internal.structle.StructleTypes.LxProtocolTypeBase;
import com.github.besherman.lifx.impl.entities.internal.structle.StructleTypes.UInt64;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Set;

public class LFXMessage {
    private static final String PAYLOAD_SIZE_METHOD_NAME = "getPayloadSize";

    private static final short ADDRESSABLE_BIT = 0x1000;
    private static final short TAGGED_BIT = 0x2000;
    private static final int PROTOCOL_VERSION_BITS = 0x0FFF;

    //private static final short ACKNOWLEDGEMENT_BIT = 0x0001;
    private static final int LX_PROTOCOL_V1 = 1024;
    private static final int CURRENT_PROTOCOL = LX_PROTOCOL_V1;
    private static final int BASE_MESSAGE_SIZE = 36;
    private static final int PAYLOAD_START_INDEX = 36;    
    

    // When the message was received (incoming) or created (outgoing)
    private final long timestamp; 

    // incoming/outgoing
    private final LFXMessageDirection messageDirection; 

    private final Type messageType;

    // For outgoing messages
    private final LFXTarget target;

    // LIFX Protocol Header Properties
    private final int size;
    
    private final int protocol;
    
    private final long atTime;
    
    private final LFXBinaryPath path;
    
    
    private final LxProtocolTypeBase payload;
    
    // Network host (this will be set by the Message Router to be the host of the receiving
    // network connection). For outgoing messages, this will be nil.
    private final InetAddress sourceNetworkHost;

    // 
    private final int incomingHash;
    
    /**
     * Creates a new message with specific type. Used for outgoing messages.
     */
    public LFXMessage(Type type) {
        this.timestamp = System.currentTimeMillis();
        this.messageDirection = LFXMessageDirection.OUTGOING;
        this.protocol = CURRENT_PROTOCOL;
        this.path = new LFXBinaryPath(new LFXSiteID(), new LFXBinaryTargetID());
        this.payload = null;
        this.messageType = type;
        
        // final 
        this.sourceNetworkHost = null;
        this.target = null;
        this.size = 0;
        this.atTime = 0;
        this.incomingHash = 0;
    }

    /**
     * Creates a new message with type and target. Used for outgoing messages.
     */
    public LFXMessage(Type type, LFXTarget target) {
        this(type, target, null);
    }

    /**
     * Creates a new message with type, target and payload. Used for outgoing messages.
     */
    public LFXMessage(Type type, LFXTarget target, LxProtocolTypeBase payload) {
        this.timestamp = System.currentTimeMillis();
        this.messageDirection = LFXMessageDirection.OUTGOING;
        this.protocol = CURRENT_PROTOCOL;
        this.path = new LFXBinaryPath(new LFXSiteID(), new LFXBinaryTargetID());
        this.payload = payload;
        this.messageType = type;        
        
        this.target = target;
        
        // final 
        this.sourceNetworkHost = null;
        this.size = 0;
        this.atTime = 0;        
        this.incomingHash = 0;
    }    

    public LFXMessage(Type type, LFXBinaryPath path) {
        this(type, path, null);        
    }
    
    public LFXMessage(Type type, LFXBinaryPath path, LxProtocolTypeBase payload) {
        this.timestamp = System.currentTimeMillis();
        this.messageDirection = LFXMessageDirection.OUTGOING;
        this.protocol = CURRENT_PROTOCOL;        
        this.payload = payload;
        this.messageType = type;        
        this.path = path;
        
        // final 
        this.sourceNetworkHost = null;
        this.target = null;
        this.size = 0;
        this.atTime = 0;        
        this.incomingHash = 0;
    }
    
    public LFXMessage(byte[] data) {
        if (data == null || data.length == 0) {
            throw new RuntimeException("data is missing");
        }

        byte[] bytes = new byte[data.length];
        LFXByteUtils.copyBytesIntoByteArray(bytes, data);

        if (!messageIsAddressable(bytes)) {
            // We don't know how to deal with non-addressable messages, but the bulbs are sometimes not setting this flag correctly
            throw new RuntimeException("Message claims to be non-addressable");
        }

        int protocol = getProtocolFromMessageData(bytes);
        if (protocol != CURRENT_PROTOCOL) {
            
            // TODO: can't we just remove this?
            
            Logger.getLogger(LFXMessage.class.getName()).log(Level.WARNING, "Handling non-protocol message of protocol {0}", protocol);
            //return LFXMessageA.initWithNonProtocolMessageData(data);
            this.messageType = getTypeFromMessageData(data);

            this.timestamp = System.currentTimeMillis();
            this.messageDirection = LFXMessageDirection.INCOMING;

            byte[] bytes_2 = new byte[data.length];
            LFXByteUtils.copyBytesIntoByteArray(bytes_2, data);

            this.size = getSizeFromMessageData(data);
            this.protocol = getProtocolFromMessageData(data);

            LFXSiteID site = new LFXSiteID(getSiteIDFromMessageData(data));

            LFXBinaryTargetID target = null;
            if (getIsTaggedFromMessageData(bytes_2)) {            
                Set<LFXTagID> tags = LFXTagID.unpack(new UInt64(getTargetFromMessageData(bytes_2)));
                target = new LFXBinaryTargetID(tags);
            } else {
                LFXDeviceID deviceId = new LFXDeviceID(getTargetFromMessageData(bytes_2));
                target = new LFXBinaryTargetID(deviceId);
            }
            this.path = new LFXBinaryPath(site, target);                
            
            // final 
            this.atTime = 0;
            this.payload = null;
            this.incomingHash = 0;
        } else {        
            this.messageType = getTypeFromMessageData(data);

            this.timestamp = System.currentTimeMillis();
            this.messageDirection = LFXMessageDirection.INCOMING;

            byte[] bytes_1 = new byte[data.length];
            LFXByteUtils.copyBytesIntoByteArray(bytes_1, data);

            this.size = getSizeFromMessageData(bytes_1);
            this.protocol = getProtocolFromMessageData(bytes_1);
            this.atTime = getAtTimeFromMessageData(bytes_1);
            this.incomingHash = getHashFromMessageData(bytes_1);

            LFXSiteID site_1 = new LFXSiteID(getSiteIDFromMessageData(bytes_1));

            LFXBinaryTargetID target_1;
            if (getIsTaggedFromMessageData(bytes_1)) {            
                Set<LFXTagID> tags = LFXTagID.unpack(new UInt64(getTargetFromMessageData(bytes_1)));
                target_1 = new LFXBinaryTargetID(tags);
            } else {
                LFXDeviceID deviceId = new LFXDeviceID(getTargetFromMessageData(bytes_1));
                target_1 = new LFXBinaryTargetID(deviceId);
            }

            this.path = new LFXBinaryPath(site_1, target_1);

            this.payload = getPayloadFromMessageData(bytes_1);            
        }        
        
        // final 
        this.sourceNetworkHost = null;
        this.target = null;        
    }    

    private LFXMessage(LFXMessage other, LFXBinaryPath path, InetAddress sourceNetworkHost) {
        this.timestamp = other.timestamp;
        this.messageDirection = other.messageDirection;
        this.messageType = other.messageType;
        this.target = other.target;
        this.size = other.size;
        this.protocol = other.protocol;
        this.atTime = other.atTime;
        this.path = path;
        this.payload = other.payload;
        this.sourceNetworkHost = sourceNetworkHost;
        this.incomingHash = 0;
    }
    
    
    
    public static int getSizeFromMessageData(byte[] data) {
        int size = StructleTypes.getShortValue(data[0], data[1]);
        return size;
    }    
    
    public byte[] getMessageDataRepresentation() {
        byte[] data;

        if (payload != null) {
            data = new byte[getMessageDataRepresentationLength()];
        } else {
            data = new byte[BASE_MESSAGE_SIZE];
        }

        writeSizeToMessage((short) data.length, data);
        writeProtocolToMessage((short) protocol, data);
        writeIsAddressableToMessage(true, data);
        writeAtTimeToMessage(atTime, data);
        writeTypeToMessage(messageType, data);

        if(LFXMessage.getTypeFromMessageData(data) != messageType) {
            throw new IllegalStateException("failed to encode the message type properly");
        }
        
        
        writeSiteIDToMessage(path.getSiteID().getDataValue(), data);

        if (path.getBinaryTargetID().geTargetType() == LFXBinaryTargetType.DEVICE) {
            writeTargetIDtoMessage(path.getBinaryTargetID().getDeviceID().getDeviceDataValue(), data);
            writeIsTaggedToMessage(false, data);
        } else {
            UInt64 tags = LFXTagID.pack(path.getBinaryTargetID().getGroupTagField());
            //writeTargetIDtoMessage(path.getBinaryTargetID().getGroupTagField().tagData, data);
            writeTargetIDtoMessage(tags.getBytes(), data);
            writeIsTaggedToMessage(true, data);
        }

        if (payload != null) {
            byte[] payloadData = payload.getBytes();

            LFXByteUtils.copyBytesIntoByteArrayAtOffset(data, payloadData, PAYLOAD_START_INDEX);
        }
        
        
        
        if(LFXMessage.getTypeFromMessageData(data) != messageType) {
            throw new IllegalStateException("failed to encode the message type properly");
        }
        
        return data;
    }    
    
    public long getTimestamp() {
        return timestamp;
    }

    public Type getType() {
        return messageType;
    }
    
    /**
     * Returns a hash of the data (excluding the time) from an incoming message.
     * This is 0 if no hash is available.
     * 
     * TODO: remove this?
     */
    public int getIncomingHash() {
        return incomingHash;
    }

    public boolean isAResponseMessage() {
        switch(getType()) {
            case LX_PROTOCOL_DEVICE_STATE_PAN_GATEWAY:
            case LX_PROTOCOL_DEVICE_STATE_TIME:
            case LX_PROTOCOL_DEVICE_STATE_RESET_SWITCH:
            case LX_PROTOCOL_DEVICE_STATE_DUMMY_LOAD:
            case LX_PROTOCOL_DEVICE_STATE_MESH_INFO:
            case LX_PROTOCOL_DEVICE_STATE_MESH_FIRMWARE:
            case LX_PROTOCOL_DEVICE_STATE_WIFI_INFO:
            case LX_PROTOCOL_DEVICE_STATE_WIFI_FIRMWARE:
            case LX_PROTOCOL_DEVICE_STATE_POWER:
            case LX_PROTOCOL_DEVICE_STATE_LABEL:
            case LX_PROTOCOL_DEVICE_STATE_TAGS:
            case LX_PROTOCOL_DEVICE_STATE_TAG_LABELS:
            case LX_PROTOCOL_DEVICE_STATE_VERSION:
            case LX_PROTOCOL_DEVICE_STATE_INFO:
            case LX_PROTOCOL_DEVICE_STATE_MCU_RAIL_VOLTAGE:
            case LX_PROTOCOL_DEVICE_STATE_FACTORY_TEST_MODE:
            case LX_PROTOCOL_LIGHT_STATE:
            case LX_PROTOCOL_LIGHT_STATE_RAIL_VOLTAGE:
            case LX_PROTOCOL_LIGHT_STATE_TEMPERATURE:
            case LX_PROTOCOL_LIGHT_STATE_SIMPLE_EVENT:
            case LX_PROTOCOL_LIGHT_STATE_POWER:
            //case LX_PROTOCOL_WAN_STATE:
            //case LX_PROTOCOL_WAN_STATE_AUTH_KEY:
            //case LX_PROTOCOL_WAN_STATE_KEEP_ALIVE:
            //case LX_PROTOCOL_WAN_STATE_HOST:
            case LX_PROTOCOL_WIFI_STATE:
            //case LX_PROTOCOL_WIFI_STATE_ACCESS_POINTS:
            case LX_PROTOCOL_WIFI_STATE_ACCESS_POINT:
            case LX_PROTOCOL_SENSOR_STATE_AMBIENT_LIGHT:
            case LX_PROTOCOL_SENSOR_STATE_DIMMER_VOLTAGE:            
                return true;
        }
        return false;        
    }


    public <T extends LxProtocolTypeBase> T getPayload() {
        return (T)payload;
    }

    public LFXBinaryPath getPath() {
        return path;
    }
    
    public InetAddress getSourceNetworkHost() {
        return sourceNetworkHost;
    }
    
    public LFXTarget getTarget() {
        return target;
    }
    
    public LFXMessage withPath(LFXBinaryPath path) {
        return new LFXMessage(this, path, sourceNetworkHost);
    }
    
    public LFXMessage withSource(InetAddress sourceNetworkHost) {
        return new LFXMessage(this, path, sourceNetworkHost);
    }    
    
    
    ////////////////////////////////////////////////////////////////////////////
    // Static methods
    ////////////////////////////////////////////////////////////////////////////
    
    private static boolean messageIsAddressable(byte[] data) {
        boolean addressable = false;
        short protocol = StructleTypes.getShortValue(data[2], data[3]);

        if ((protocol & ADDRESSABLE_BIT) != 0) {
            addressable = true;
        } else {
            addressable = false;
        }

        return addressable;
    }

    private static int getProtocolFromMessageData(byte[] data) {
        short protocol = StructleTypes.getShortValue(data[2], data[3]);
        int protocolVersion = (protocol & PROTOCOL_VERSION_BITS);
        return protocolVersion;
    }

    private static long getAtTimeFromMessageData(byte[] data) {
        byte[] atTimeArray = new byte[8];

        atTimeArray[0] = data[24];
        atTimeArray[1] = data[25];
        atTimeArray[2] = data[26];
        atTimeArray[3] = data[27];
        atTimeArray[4] = data[28];
        atTimeArray[5] = data[29];
        atTimeArray[6] = data[30];
        atTimeArray[7] = data[31];

        return StructleTypes.getLongValue(atTimeArray[0], atTimeArray[1], atTimeArray[2], 
                atTimeArray[3], atTimeArray[4], atTimeArray[5], atTimeArray[6], atTimeArray[7]);
    }
    
    /**
     * Returns a hash of the message data, excluding the time.
     */
    private static int getHashFromMessageData(byte[] _data) {
        byte[] copy = Arrays.copyOf(_data, _data.length);

        // clear out atTime because that will be different every time even
        // if the message is the same
        copy[24] = 0;
        copy[25] = 0;
        copy[26] = 0;
        copy[27] = 0;
        copy[28] = 0;
        copy[29] = 0;
        copy[30] = 0;
        copy[31] = 0;
        
        return LFXByteUtils.byteArrayToHexString(copy).hashCode();
    }

    private static byte[] getSiteIDFromMessageData(byte[] data) {
        byte[] siteIdArray = new byte[6];
        siteIdArray[0] = data[16];
        siteIdArray[1] = data[17];
        siteIdArray[2] = data[18];
        siteIdArray[3] = data[19];
        siteIdArray[4] = data[20];
        siteIdArray[5] = data[21];

        return siteIdArray;
    }

    private static byte[] getTargetFromMessageData(byte[] data) {
        boolean tagged = getIsTaggedFromMessageData(data);
        byte[] targetByteArray = null;

        if (tagged) {
            targetByteArray = new byte[8];
            targetByteArray[0] = data[8];
            targetByteArray[1] = data[9];
            targetByteArray[2] = data[10];
            targetByteArray[3] = data[11];
            targetByteArray[4] = data[12];
            targetByteArray[5] = data[13];
            targetByteArray[6] = data[14];
            targetByteArray[7] = data[15];
        } else {
            targetByteArray = new byte[6];
            targetByteArray[0] = data[8];
            targetByteArray[1] = data[9];
            targetByteArray[2] = data[10];
            targetByteArray[3] = data[11];
            targetByteArray[4] = data[12];
            targetByteArray[5] = data[13];
        }

        return targetByteArray;
    }

    private static boolean getIsTaggedFromMessageData(byte[] data) {
        short protocol = StructleTypes.getShortValue(data[2], data[3]);
        boolean tagged = false;

        if ((protocol & TAGGED_BIT) != 0) {
            tagged = true;
        } else {
            tagged = false;
        }

        return tagged;
    }

    private static Type getTypeFromMessageData(byte[] data) {
        short typeValue = StructleTypes.getShortValue(data[32], data[33]);
        Type type = LxProtocol.typeMap.get((int) typeValue);
        if(type == null) {
            throw new IllegalStateException("message data has type value=" + typeValue + " which is not in LxProtocol.typeMap");
        }

        return type;
    }

    private static LxProtocolTypeBase getPayloadFromMessageData(byte[] data) {
        if(data == null) {
            throw new IllegalArgumentException("data can not be null");
        }
        LxProtocol.Type messageType = getTypeFromMessageData(data);        
        if(messageType == null) {
            throw new IllegalStateException("messageType is null in message data");
        }
        
        Class<? extends LxProtocolTypeBase> messagePayloadClass = LxProtocol.typeClassMap.get(messageType);
        if(messagePayloadClass == null) {
            throw new IllegalStateException(messageType + " is missing from LxProtocol.typeClassMap");
        }

        LxProtocolTypeBase payload = null;        
        try {
            payload = messagePayloadClass.getDeclaredConstructor(new Class[]{byte[].class, int.class}).newInstance(data, PAYLOAD_START_INDEX);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
            String msg = String.format("Faild to create class %s from payload %s", messagePayloadClass.getName(), StructleTypes.bytesToString(data));
            Logger.getLogger(LFXMessage.class.getName()).log(Level.SEVERE, msg, e);
        }

        return payload;
    }


    private int getMessageDataRepresentationLength() {
        int prePayloadLength = BASE_MESSAGE_SIZE;

        Class<? extends LxProtocolTypeBase> payloadClass = LxProtocol.typeClassMap.get(messageType);
        int payloadLength = 0;

        try {
            Method method = payloadClass.getMethod(PAYLOAD_SIZE_METHOD_NAME);
            Object o = method.invoke(null);

            if (o != null) {
                payloadLength = (Integer) o;
            }
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException e) {
            Logger.getLogger(LFXMessage.class.getName()).log(Level.SEVERE, null, e);
        }

        return (payloadLength + prePayloadLength);
    }

    private static void writeSizeToMessage(short size, byte[] data) {
        data[0] = (byte) (size & 0xff);
        data[1] = (byte) ((size >> 8) & 0xff);
    }

    private static void writeProtocolToMessage(short protocol, byte[] data) {
        short protocolField = StructleTypes.getShortValue(data[2], data[3]);

        short version = (short) (protocol & PROTOCOL_VERSION_BITS);

        protocolField = (short) (protocolField | version);

        data[2] = (byte) (protocolField & 0xff);
        data[3] = (byte) ((protocolField >> 8) & 0xff);
    }

    private static void writeIsAddressableToMessage(boolean isAddressable, byte[] data) {
        short protocolField = StructleTypes.getShortValue(data[2], data[3]);

        if (isAddressable) {
            protocolField = (short) (protocolField | ADDRESSABLE_BIT);
        }

        data[2] = (byte) (protocolField & 0xff);
        data[3] = (byte) ((protocolField >> 8) & 0xff);
    }

    private static void writeAtTimeToMessage(long atTime, byte[] data) {
        UInt64 wrappedAtTime = new UInt64(atTime);
        byte[] atTimeBytes = wrappedAtTime.getBytes();

        data[24] = atTimeBytes[0];
        data[25] = atTimeBytes[1];
        data[26] = atTimeBytes[2];
        data[27] = atTimeBytes[3];
        data[28] = atTimeBytes[4];
        data[29] = atTimeBytes[5];
        data[30] = atTimeBytes[6];
        data[31] = atTimeBytes[7];
    }

    private static void writeTypeToMessage(Type type, byte[] data) {
        int typeValue = LxProtocol.typeValueMap.get(type);
//        short typeValueShort = (short) typeValue;
//        data[32] = (byte) (typeValueShort & 0xff);
//        data[33] = (byte) ((typeValueShort >> 8) & 0xff);
        data[32] = (byte) (typeValue & 0xff);
        data[33] = (byte) ((typeValue >> 8) & 0xff);
        
        short enc = StructleTypes.getShortValue(data[32], data[33]);
        if(typeValue != enc) {
            throw new IllegalStateException("failed to encode short, was " + typeValue + " as integer, and became " + enc);
        }
    }

    private static void writeSiteIDToMessage(byte[] siteID, byte[] data) {
        int siteOffsetIndex = 16;
        for (int i = 0; i < siteID.length; i++) {
            data[siteOffsetIndex + i] = siteID[i];
        }
    }

    private static void writeTargetIDtoMessage(byte[] targetID, byte[] data) {
        int targetOffsetIndex = 8;
        for (int i = 0; i < targetID.length; i++) {
            data[targetOffsetIndex + i] = targetID[i];
        }
    }

    private static void writeIsTaggedToMessage(boolean tagged, byte[] data) {
        short protocolField = StructleTypes.getShortValue(data[2], data[3]);

        if (tagged) {
            protocolField = (short) (protocolField | TAGGED_BIT);
        }

        data[2] = (byte) (protocolField & 0xff);
        data[3] = (byte) ((protocolField >> 8) & 0xff);
    }
}
