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
package com.github.besherman.lifx.impl.entities.internal.structle;

import java.util.HashMap;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.besherman.lifx.impl.entities.internal.structle.StructleTypes.Bool8;
import com.github.besherman.lifx.impl.entities.internal.structle.StructleTypes.Int16;
import com.github.besherman.lifx.impl.entities.internal.structle.StructleTypes.LxProtocolTypeBase;
import com.github.besherman.lifx.impl.entities.internal.structle.StructleTypes.UInt16;
import com.github.besherman.lifx.impl.entities.internal.structle.StructleTypes.UInt32;
import com.github.besherman.lifx.impl.entities.internal.structle.StructleTypes.UInt8;

@SuppressWarnings("unused")
public class LxProtocolWifi {

    ////////////////////////////////////////////////////////////////////////////
    // Lx::Protocol::Wifi::Interface
    ////////////////////////////////////////////////////////////////////////////
    public enum Interface {
        LX_PROTOCOL_WIFI_INTERFACE_SOFT_AP, // LX_PROTOCOL_WIFI_INTERFACE_SOFT_AP = 1
        LX_PROTOCOL_WIFI_INTERFACE_STATION,				// LX_PROTOCOL_WIFI_INTERFACE_STATION = 2
    };

    public static final HashMap<Interface, Integer> interfaceValueMap;
    public static final HashMap<Integer, Interface> interfaceMap;

    ////////////////////////////////////////////////////////////////////////////
    // Enum Lx::Protocol::Wifi::Security
    ////////////////////////////////////////////////////////////////////////////
    public enum Security {
        LX_PROTOCOL_WIFI_SECURITY_UNKNOWN, // LX_PROTOCOL_WIFI_SECURITY_UNKNOWN = 0
        LX_PROTOCOL_WIFI_SECURITY_OPEN, // LX_PROTOCOL_WIFI_SECURITY_OPEN = 1
        LX_PROTOCOL_WIFI_SECURITY_WEP_PSK, // LX_PROTOCOL_WIFI_SECURITY_WEP_PSK = 2
        LX_PROTOCOL_WIFI_SECURITY_WPA_TKIP_PSK, // LX_PROTOCOL_WIFI_SECURITY_WPA_TKIP_PSK = 3
        LX_PROTOCOL_WIFI_SECURITY_WPA_AES_PSK, // LX_PROTOCOL_WIFI_SECURITY_WPA_AES_PSK = 4
        LX_PROTOCOL_WIFI_SECURITY_WPA2_AES_PSK, // LX_PROTOCOL_WIFI_SECURITY_WPA2_AES_PSK = 5
        LX_PROTOCOL_WIFI_SECURITY_WPA2_TKIP_PSK, // LX_PROTOCOL_WIFI_SECURITY_WPA2_TKIP_PSK = 6
        LX_PROTOCOL_WIFI_SECURITY_WPA2_MIXED_PSK,				// LX_PROTOCOL_WIFI_SECURITY_WPA2_MIXED_PSK = 7
    };

    public static final HashMap<Security, Integer> securityValueMap;
    public static final HashMap<Integer, Security> securityMap;

    ////////////////////////////////////////////////////////////////////////////
    // Lx::Protocol::Wifi::Status
    ////////////////////////////////////////////////////////////////////////////
    public enum Status {
        LX_PROTOCOL_WIFI_STATUS_CONNECTING, // LX_PROTOCOL_WIFI_STATUS_CONNECTING = 0
        LX_PROTOCOL_WIFI_STATUS_CONNECTED, // LX_PROTOCOL_WIFI_STATUS_CONNECTED = 1
        LX_PROTOCOL_WIFI_STATUS_FAILED, // LX_PROTOCOL_WIFI_STATUS_FAILED = 2
        LX_PROTOCOL_WIFI_STATUS_OFF,				// LX_PROTOCOL_WIFI_STATUS_OFF = 3
    };

    public static final HashMap<Status, Integer> statusValueMap;
    public static final HashMap<Integer, Status> statusMap;

    static {
        interfaceValueMap = new HashMap<Interface, Integer>();
        interfaceMap = new HashMap<Integer, Interface>();
        interfaceValueMap.put(Interface.LX_PROTOCOL_WIFI_INTERFACE_SOFT_AP, 1);
        interfaceMap.put(1, Interface.LX_PROTOCOL_WIFI_INTERFACE_SOFT_AP);
        interfaceValueMap.put(Interface.LX_PROTOCOL_WIFI_INTERFACE_STATION, 2);
        interfaceMap.put(2, Interface.LX_PROTOCOL_WIFI_INTERFACE_STATION);

        securityValueMap = new HashMap<Security, Integer>();
        securityMap = new HashMap<Integer, Security>();
        securityValueMap.put(Security.LX_PROTOCOL_WIFI_SECURITY_UNKNOWN, 0);
        securityMap.put(0, Security.LX_PROTOCOL_WIFI_SECURITY_UNKNOWN);
        securityValueMap.put(Security.LX_PROTOCOL_WIFI_SECURITY_OPEN, 1);
        securityMap.put(1, Security.LX_PROTOCOL_WIFI_SECURITY_OPEN);
        securityValueMap.put(Security.LX_PROTOCOL_WIFI_SECURITY_WEP_PSK, 2);
        securityMap.put(2, Security.LX_PROTOCOL_WIFI_SECURITY_WEP_PSK);
        securityValueMap.put(Security.LX_PROTOCOL_WIFI_SECURITY_WPA_TKIP_PSK, 3);
        securityMap.put(3, Security.LX_PROTOCOL_WIFI_SECURITY_WPA_TKIP_PSK);
        securityValueMap.put(Security.LX_PROTOCOL_WIFI_SECURITY_WPA_AES_PSK, 4);
        securityMap.put(4, Security.LX_PROTOCOL_WIFI_SECURITY_WPA_AES_PSK);
        securityValueMap.put(Security.LX_PROTOCOL_WIFI_SECURITY_WPA2_AES_PSK, 5);
        securityMap.put(5, Security.LX_PROTOCOL_WIFI_SECURITY_WPA2_AES_PSK);
        securityValueMap.put(Security.LX_PROTOCOL_WIFI_SECURITY_WPA2_TKIP_PSK, 6);
        securityMap.put(6, Security.LX_PROTOCOL_WIFI_SECURITY_WPA2_TKIP_PSK);
        securityValueMap.put(Security.LX_PROTOCOL_WIFI_SECURITY_WPA2_MIXED_PSK, 7);
        securityMap.put(7, Security.LX_PROTOCOL_WIFI_SECURITY_WPA2_MIXED_PSK);

        statusValueMap = new HashMap<Status, Integer>();
        statusMap = new HashMap<Integer, Status>();
        statusValueMap.put(Status.LX_PROTOCOL_WIFI_STATUS_CONNECTING, 0);
        statusMap.put(0, Status.LX_PROTOCOL_WIFI_STATUS_CONNECTING);
        statusValueMap.put(Status.LX_PROTOCOL_WIFI_STATUS_CONNECTED, 1);
        statusMap.put(1, Status.LX_PROTOCOL_WIFI_STATUS_CONNECTED);
        statusValueMap.put(Status.LX_PROTOCOL_WIFI_STATUS_FAILED, 2);
        statusMap.put(2, Status.LX_PROTOCOL_WIFI_STATUS_FAILED);
        statusValueMap.put(Status.LX_PROTOCOL_WIFI_STATUS_OFF, 3);
        statusMap.put(3, Status.LX_PROTOCOL_WIFI_STATUS_OFF);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Lx::Protocol::Wifi::Get 
    ////////////////////////////////////////////////////////////////////////////
    public static class Get extends LxProtocolTypeBase { 
        // Fields: interface;

        private UInt8 interfacetype;			// Field: interfacetype - Structle::Uint8 byte offset: 0

        private static final int PAYLOAD_SIZE = 1;

        public Get(byte[] bytes) {
            this(bytes, 0);
        }

        public Get(byte[] bytes, int initialOffset) {
            if(bytes.length != 36 + PAYLOAD_SIZE) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, 
                        String.format("payload has more data than advertised: %s", StructleTypes.bytesToString(bytes)));
            }                                    
            
            byte[] member0Data = new byte[1];
            member0Data[0] = bytes[initialOffset + 0];

            interfacetype = new UInt8(member0Data);

        }

        public Get(Object padding, UInt8 interfacetype) {
            this.interfacetype = interfacetype;
        }

        public UInt8 getInterfacetype() {
            return interfacetype;
        }

        @Override
        public void printMessageData() {
            interfacetype.printValue("interfacetype");			// Field: interfacetype - Structle::Uint8 byte offset: 1
        }

        public static void loadMessageDataWithPayloadAtOffset(byte[] messageData, int offset, UInt8 interfacetype) {
            byte[] memberData;		// = name.getBytes();

            memberData = interfacetype.getBytes();

            for (int i = 0; i < (memberData.length); i++) {
                messageData[(offset + i)] = memberData[i];
            }

            offset += memberData.length;
        }

        public static void loadMessageDataWithPayloadAtDefaultOffset(byte[] messageData, UInt8 interfacetype) {
            int offset = PAYLOAD_OFFSET;

            loadMessageDataWithPayloadAtOffset(messageData, offset, interfacetype);
        }

        @Override
        public byte[] getBytes() {
            int offset = 0;

            byte[] bytes = new byte[getPayloadSize()];

            byte[] memberData;

            // = name.getBytes();        		
            memberData = interfacetype.getBytes();

            for (int i = 0; i < (memberData.length); i++) {
                bytes[(offset + i)] = memberData[i];
            }

            offset += memberData.length;

            return bytes;
        }

        public static int getPayloadSize() {
            return PAYLOAD_SIZE;
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Lx::Protocol::Wifi::Set 
    ////////////////////////////////////////////////////////////////////////////
    public static class Set extends LxProtocolTypeBase { 
        // Fields: interface, active;

        private UInt8 interfacetype;			// Field: interfacetype - Structle::Uint8 byte offset: 0
        private Bool8 active;			// Field: active - Structle::Bool byte offset: 1

        private static final int PAYLOAD_SIZE = 2;

        public Set(byte[] bytes) {
            this(bytes, 0);
        }

        public Set(byte[] bytes, int initialOffset) {
            if(bytes.length != 36 + PAYLOAD_SIZE) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, 
                        String.format("payload has more data than advertised: %s", StructleTypes.bytesToString(bytes)));
            }                                    
            
            byte[] member0Data = new byte[1];
            member0Data[0] = bytes[initialOffset + 0];

            interfacetype = new UInt8(member0Data);

            byte[] member1Data = new byte[1];
            member1Data[0] = bytes[initialOffset + 1];

            active = new Bool8(member1Data);

        }

        public Set(Object padding, UInt8 interfacetype, Bool8 active) {
            this.interfacetype = interfacetype;
            this.active = active;
        }

        public UInt8 getInterfacetype() {
            return interfacetype;
        }

        public Bool8 getActive() {
            return active;
        }

        @Override
        public void printMessageData() {
            interfacetype.printValue("interfacetype");			// Field: interfacetype - Structle::Uint8 byte offset: 2
            active.printValue("active");			// Field: active - Structle::Bool byte offset: 2
        }

        public static void loadMessageDataWithPayloadAtOffset(byte[] messageData, int offset, UInt8 interfacetype, Bool8 active) {
            byte[] memberData;		// = name.getBytes();

            memberData = interfacetype.getBytes();

            for (int i = 0; i < (memberData.length); i++) {
                messageData[(offset + i)] = memberData[i];
            }

            offset += memberData.length;

            memberData = active.getBytes();

            for (int i = 0; i < (memberData.length); i++) {
                messageData[(offset + i)] = memberData[i];
            }

            offset += memberData.length;
        }

        public static void loadMessageDataWithPayloadAtDefaultOffset(byte[] messageData, UInt8 interfacetype, Bool8 active) {
            int offset = PAYLOAD_OFFSET;
            loadMessageDataWithPayloadAtOffset(messageData, offset, interfacetype, active);
        }

        @Override
        public byte[] getBytes() {
            int offset = 0;

            byte[] bytes = new byte[getPayloadSize()];

            byte[] memberData;

            // = name.getBytes();        		
            memberData = interfacetype.getBytes();

            for (int i = 0; i < (memberData.length); i++) {
                bytes[(offset + i)] = memberData[i];
            }

            offset += memberData.length;
            // = name.getBytes();        		
            memberData = active.getBytes();

            for (int i = 0; i < (memberData.length); i++) {
                bytes[(offset + i)] = memberData[i];
            }

            offset += memberData.length;

            return bytes;
        }

        public static int getPayloadSize() {
            return PAYLOAD_SIZE;
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Lx::Protocol::Wifi::State 
    ////////////////////////////////////////////////////////////////////////////
    public static class State extends LxProtocolTypeBase { 
        // Fields: interface, status, ipv4, ipv6;

        private UInt8 interfacetype;			// Field: interfacetype - Structle::Uint8 byte offset: 0
        private UInt8 status;			// Field: status - Structle::Uint8 byte offset: 1
        private UInt32 ipv4;			// Field: ipv4 - Structle::Uint32 byte offset: 2
        private byte[] ipv6 = new byte[16];		// Field: ipv6 - Structle::Bytes byte offset: 6

        private static final int PAYLOAD_SIZE = 22;

        public State(byte[] bytes) {
            this(bytes, 0);
        }

        public State(byte[] bytes, int initialOffset) {
            if(bytes.length != 36 + PAYLOAD_SIZE) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, 
                        String.format("payload has more data than advertised: %s", StructleTypes.bytesToString(bytes)));
            }                                    
            
            byte[] member0Data = new byte[1];
            member0Data[0] = bytes[initialOffset + 0];

            interfacetype = new UInt8(member0Data);

            byte[] member1Data = new byte[1];
            member1Data[0] = bytes[initialOffset + 1];

            status = new UInt8(member1Data);

            byte[] member2Data = new byte[4];
            member2Data[0] = bytes[initialOffset + 2];
            member2Data[1] = bytes[initialOffset + 3];
            member2Data[2] = bytes[initialOffset + 4];
            member2Data[3] = bytes[initialOffset + 5];

            ipv4 = new UInt32(member2Data);

            byte[] member3Data = new byte[16];
            member3Data[0] = bytes[initialOffset + 6];
            member3Data[1] = bytes[initialOffset + 7];
            member3Data[2] = bytes[initialOffset + 8];
            member3Data[3] = bytes[initialOffset + 9];
            member3Data[4] = bytes[initialOffset + 10];
            member3Data[5] = bytes[initialOffset + 11];
            member3Data[6] = bytes[initialOffset + 12];
            member3Data[7] = bytes[initialOffset + 13];
            member3Data[8] = bytes[initialOffset + 14];
            member3Data[9] = bytes[initialOffset + 15];
            member3Data[10] = bytes[initialOffset + 16];
            member3Data[11] = bytes[initialOffset + 17];
            member3Data[12] = bytes[initialOffset + 18];
            member3Data[13] = bytes[initialOffset + 19];
            member3Data[14] = bytes[initialOffset + 20];
            member3Data[15] = bytes[initialOffset + 21];

            ipv6 = member3Data;

        }

        public State(Object padding, UInt8 interfacetype, UInt8 status, UInt32 ipv4, byte[] ipv6) {
            this.interfacetype = interfacetype;
            this.status = status;
            this.ipv4 = ipv4;
            this.ipv6 = ipv6;
        }

        public UInt8 getInterfacetype() {
            return interfacetype;
        }

        public UInt8 getStatus() {
            return status;
        }

        public UInt32 getIpv4() {
            return ipv4;
        }

        public byte[] getIpv6() {
            return ipv6;
        }

        @Override
        public void printMessageData() {
            interfacetype.printValue("interfacetype");			// Field: interfacetype - Structle::Uint8 byte offset: 22
            status.printValue("status");			// Field: status - Structle::Uint8 byte offset: 22
            ipv4.printValue("ipv4");			// Field: ipv4 - Structle::Uint32 byte offset: 22
            Logger.getLogger(State.class.getName()).log(Level.FINE, "Byte Array Print not currently supported");
        }

        public static void loadMessageDataWithPayloadAtOffset(byte[] messageData, int offset, UInt8 interfacetype, UInt8 status, UInt32 ipv4, byte[] ipv6) {
            byte[] memberData;		// = name.getBytes();

            memberData = interfacetype.getBytes();

            for (int i = 0; i < (memberData.length); i++) {
                messageData[(offset + i)] = memberData[i];
            }

            offset += memberData.length;

            memberData = status.getBytes();

            for (int i = 0; i < (memberData.length); i++) {
                messageData[(offset + i)] = memberData[i];
            }

            offset += memberData.length;

            memberData = ipv4.getBytes();

            for (int i = 0; i < (memberData.length); i++) {
                messageData[(offset + i)] = memberData[i];
            }

            offset += memberData.length;

            memberData = ipv6;

            for (int i = 0; i < (memberData.length); i++) {
                messageData[(offset + i)] = memberData[i];
            }

            offset += memberData.length;
        }

        public static void loadMessageDataWithPayloadAtDefaultOffset(byte[] messageData, UInt8 interfacetype, UInt8 status, UInt32 ipv4, byte[] ipv6) {
            int offset = PAYLOAD_OFFSET;
            loadMessageDataWithPayloadAtOffset(messageData, offset, interfacetype, status, ipv4, ipv6);
        }

        @Override
        public byte[] getBytes() {
            int offset = 0;

            byte[] bytes = new byte[getPayloadSize()];

            byte[] memberData;

            // = name.getBytes();        		
            memberData = interfacetype.getBytes();

            for (int i = 0; i < (memberData.length); i++) {
                bytes[(offset + i)] = memberData[i];
            }

            offset += memberData.length;
            // = name.getBytes();        		
            memberData = status.getBytes();

            for (int i = 0; i < (memberData.length); i++) {
                bytes[(offset + i)] = memberData[i];
            }

            offset += memberData.length;
            // = name.getBytes();        		
            memberData = ipv4.getBytes();

            for (int i = 0; i < (memberData.length); i++) {
                bytes[(offset + i)] = memberData[i];
            }

            offset += memberData.length;
            // = name.getBytes();        		
            memberData = ipv6;

            for (int i = 0; i < (memberData.length); i++) {
                bytes[(offset + i)] = memberData[i];
            }

            offset += memberData.length;

            return bytes;
        }

        public static int getPayloadSize() {
            return PAYLOAD_SIZE;
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Lx::Protocol::Wifi::GetAccessPoint 
    ////////////////////////////////////////////////////////////////////////////
    public static class GetAccessPoint extends LxProtocolTypeBase { 

        private static final int PAYLOAD_SIZE = 0;

        public GetAccessPoint(byte[] bytes) {
            this(bytes, 0);
        }

        public GetAccessPoint(byte[] bytes, int initialOffset) {
            if(bytes.length != 36 + PAYLOAD_SIZE) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, 
                        String.format("payload has more data than advertised: %s", StructleTypes.bytesToString(bytes)));
            }                                    
            
        }

        public GetAccessPoint(Object padding) {
        }

        @Override
        public void printMessageData() {
        }

        public static void loadMessageDataWithPayloadAtOffset(byte[] messageData, int offset) {
            byte[] memberData;		// = name.getBytes();
        }

        public static void loadMessageDataWithPayloadAtDefaultOffset(byte[] messageData) {
            int offset = PAYLOAD_OFFSET;
            loadMessageDataWithPayloadAtOffset(messageData, offset);
        }

        @Override
        public byte[] getBytes() {
            int offset = 0;

            byte[] bytes = new byte[getPayloadSize()];

            byte[] memberData;

            return bytes;
        }

        public static int getPayloadSize() {
            return PAYLOAD_SIZE;
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Lx::Protocol::Wifi::SetAccessPoint 
    ////////////////////////////////////////////////////////////////////////////
    public static class SetAccessPoint extends LxProtocolTypeBase { 
        // Fields: interface, ssid, pass, security;

        private UInt8 interfacetype;			// Field: interfacetype - Structle::Uint8 byte offset: 0
        private String ssid;			// Field: ssid - Structle::String byte offset: 1
        private String pass;			// Field: pass - Structle::String byte offset: 33
        private UInt8 security;			// Field: security - Structle::Uint8 byte offset: 97

        private static final int PAYLOAD_SIZE = 98;

        public SetAccessPoint(byte[] bytes) {
            this(bytes, 0);
        }

        public SetAccessPoint(byte[] bytes, int initialOffset) {
            if(bytes.length != 36 + PAYLOAD_SIZE) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, 
                        String.format("payload has more data than advertised: %s", StructleTypes.bytesToString(bytes)));
            }                                    
            
            byte[] member0Data = new byte[1];
            member0Data[0] = bytes[initialOffset + 0];

            interfacetype = new UInt8(member0Data);

            byte[] member1Data = new byte[32];
            member1Data[0] = bytes[initialOffset + 1];
            member1Data[1] = bytes[initialOffset + 2];
            member1Data[2] = bytes[initialOffset + 3];
            member1Data[3] = bytes[initialOffset + 4];
            member1Data[4] = bytes[initialOffset + 5];
            member1Data[5] = bytes[initialOffset + 6];
            member1Data[6] = bytes[initialOffset + 7];
            member1Data[7] = bytes[initialOffset + 8];
            member1Data[8] = bytes[initialOffset + 9];
            member1Data[9] = bytes[initialOffset + 10];
            member1Data[10] = bytes[initialOffset + 11];
            member1Data[11] = bytes[initialOffset + 12];
            member1Data[12] = bytes[initialOffset + 13];
            member1Data[13] = bytes[initialOffset + 14];
            member1Data[14] = bytes[initialOffset + 15];
            member1Data[15] = bytes[initialOffset + 16];
            member1Data[16] = bytes[initialOffset + 17];
            member1Data[17] = bytes[initialOffset + 18];
            member1Data[18] = bytes[initialOffset + 19];
            member1Data[19] = bytes[initialOffset + 20];
            member1Data[20] = bytes[initialOffset + 21];
            member1Data[21] = bytes[initialOffset + 22];
            member1Data[22] = bytes[initialOffset + 23];
            member1Data[23] = bytes[initialOffset + 24];
            member1Data[24] = bytes[initialOffset + 25];
            member1Data[25] = bytes[initialOffset + 26];
            member1Data[26] = bytes[initialOffset + 27];
            member1Data[27] = bytes[initialOffset + 28];
            member1Data[28] = bytes[initialOffset + 29];
            member1Data[29] = bytes[initialOffset + 30];
            member1Data[30] = bytes[initialOffset + 31];
            member1Data[31] = bytes[initialOffset + 32];

            int endOfStringIndex;
            byte[] subString;

            endOfStringIndex = member1Data.length;

            for (int i = 0; i < member1Data.length; i++) {
                if (member1Data[i] == 0x00) {
                    endOfStringIndex = i;
                    break;
                }
            }

            subString = new byte[endOfStringIndex];
            for (int i = 0; i < endOfStringIndex; i++) {
                subString[i] = member1Data[i];
            }

            ssid = new String(subString);

            byte[] member2Data = new byte[64];
            member2Data[0] = bytes[initialOffset + 33];
            member2Data[1] = bytes[initialOffset + 34];
            member2Data[2] = bytes[initialOffset + 35];
            member2Data[3] = bytes[initialOffset + 36];
            member2Data[4] = bytes[initialOffset + 37];
            member2Data[5] = bytes[initialOffset + 38];
            member2Data[6] = bytes[initialOffset + 39];
            member2Data[7] = bytes[initialOffset + 40];
            member2Data[8] = bytes[initialOffset + 41];
            member2Data[9] = bytes[initialOffset + 42];
            member2Data[10] = bytes[initialOffset + 43];
            member2Data[11] = bytes[initialOffset + 44];
            member2Data[12] = bytes[initialOffset + 45];
            member2Data[13] = bytes[initialOffset + 46];
            member2Data[14] = bytes[initialOffset + 47];
            member2Data[15] = bytes[initialOffset + 48];
            member2Data[16] = bytes[initialOffset + 49];
            member2Data[17] = bytes[initialOffset + 50];
            member2Data[18] = bytes[initialOffset + 51];
            member2Data[19] = bytes[initialOffset + 52];
            member2Data[20] = bytes[initialOffset + 53];
            member2Data[21] = bytes[initialOffset + 54];
            member2Data[22] = bytes[initialOffset + 55];
            member2Data[23] = bytes[initialOffset + 56];
            member2Data[24] = bytes[initialOffset + 57];
            member2Data[25] = bytes[initialOffset + 58];
            member2Data[26] = bytes[initialOffset + 59];
            member2Data[27] = bytes[initialOffset + 60];
            member2Data[28] = bytes[initialOffset + 61];
            member2Data[29] = bytes[initialOffset + 62];
            member2Data[30] = bytes[initialOffset + 63];
            member2Data[31] = bytes[initialOffset + 64];
            member2Data[32] = bytes[initialOffset + 65];
            member2Data[33] = bytes[initialOffset + 66];
            member2Data[34] = bytes[initialOffset + 67];
            member2Data[35] = bytes[initialOffset + 68];
            member2Data[36] = bytes[initialOffset + 69];
            member2Data[37] = bytes[initialOffset + 70];
            member2Data[38] = bytes[initialOffset + 71];
            member2Data[39] = bytes[initialOffset + 72];
            member2Data[40] = bytes[initialOffset + 73];
            member2Data[41] = bytes[initialOffset + 74];
            member2Data[42] = bytes[initialOffset + 75];
            member2Data[43] = bytes[initialOffset + 76];
            member2Data[44] = bytes[initialOffset + 77];
            member2Data[45] = bytes[initialOffset + 78];
            member2Data[46] = bytes[initialOffset + 79];
            member2Data[47] = bytes[initialOffset + 80];
            member2Data[48] = bytes[initialOffset + 81];
            member2Data[49] = bytes[initialOffset + 82];
            member2Data[50] = bytes[initialOffset + 83];
            member2Data[51] = bytes[initialOffset + 84];
            member2Data[52] = bytes[initialOffset + 85];
            member2Data[53] = bytes[initialOffset + 86];
            member2Data[54] = bytes[initialOffset + 87];
            member2Data[55] = bytes[initialOffset + 88];
            member2Data[56] = bytes[initialOffset + 89];
            member2Data[57] = bytes[initialOffset + 90];
            member2Data[58] = bytes[initialOffset + 91];
            member2Data[59] = bytes[initialOffset + 92];
            member2Data[60] = bytes[initialOffset + 93];
            member2Data[61] = bytes[initialOffset + 94];
            member2Data[62] = bytes[initialOffset + 95];
            member2Data[63] = bytes[initialOffset + 96];

            endOfStringIndex = member2Data.length;

            for (int i = 0; i < member2Data.length; i++) {
                if (member2Data[i] == 0x00) {
                    endOfStringIndex = i;
                    break;
                }
            }

            subString = new byte[endOfStringIndex];
            for (int i = 0; i < endOfStringIndex; i++) {
                subString[i] = member2Data[i];
            }

            pass = new String(subString);

            byte[] member3Data = new byte[1];
            member3Data[0] = bytes[initialOffset + 97];

            security = new UInt8(member3Data);
        }

        public SetAccessPoint(Object padding, UInt8 interfacetype, String ssid, String pass, UInt8 security) {
            this.interfacetype = interfacetype;
            this.ssid = ssid;
            this.pass = pass;
            this.security = security;
        }

        public UInt8 getInterfacetype() {
            return interfacetype;
        }

        public String getSsid() {
            return ssid;
        }

        public String getPass() {
            return pass;
        }

        public UInt8 getSecurity() {
            return security;
        }

        @Override
        public void printMessageData() {
            interfacetype.printValue("interfacetype");			// Field: interfacetype - Structle::Uint8 byte offset: 98
//  		System.out.println( ssid);			// Field: ssid - Structle::String byte offset: 98
//  		System.out.println( pass);			// Field: pass - Structle::String byte offset: 98
            Logger.getLogger(SetAccessPoint.class.getName()).log(Level.FINE, ssid);
            Logger.getLogger(SetAccessPoint.class.getName()).log(Level.FINE, pass);
            security.printValue("security");			// Field: security - Structle::Uint8 byte offset: 98
        }

        public static void loadMessageDataWithPayloadAtOffset(byte[] messageData, int offset, UInt8 interfacetype, String ssid, String pass, UInt8 security) {
            byte[] memberData;		// = name.getBytes();

            memberData = interfacetype.getBytes();

            for (int i = 0; i < (memberData.length); i++) {
                messageData[(offset + i)] = memberData[i];
            }

            offset += memberData.length;

            char[] ssidchars = ssid.toCharArray();
            //byte[] ssidBytes = new byte[ssidchars.length];
            byte[] ssidBytes = new byte[32];

            for (int i = 0; i < 32; i++) {
                ssidBytes[i] = 0x00;
            }

            for (int i = 0; i < ssidchars.length; i++) {
                ssidBytes[i] = (byte) ssidchars[i];
            }

            memberData = ssidBytes;

            for (int i = 0; i < (memberData.length); i++) {
                messageData[(offset + i)] = memberData[i];
            }

            offset += memberData.length;

            char[] passchars = pass.toCharArray();
            //byte[] passBytes = new byte[passchars.length];
            byte[] passBytes = new byte[64];

            for (int i = 0; i < 64; i++) {
                passBytes[i] = 0x00;
            }

            for (int i = 0; i < passchars.length; i++) {
                passBytes[i] = (byte) passchars[i];
            }

            memberData = passBytes;

            for (int i = 0; i < (memberData.length); i++) {
                messageData[(offset + i)] = memberData[i];
            }

            offset += memberData.length;

            memberData = security.getBytes();

            for (int i = 0; i < (memberData.length); i++) {
                messageData[(offset + i)] = memberData[i];
            }

            offset += memberData.length;
        }

        public static void loadMessageDataWithPayloadAtDefaultOffset(byte[] messageData, UInt8 interfacetype, String ssid, String pass, UInt8 security) {
            int offset = PAYLOAD_OFFSET;

            loadMessageDataWithPayloadAtOffset(messageData, offset, interfacetype, ssid, pass, security);
        }

        @Override
        public byte[] getBytes() {
            int offset = 0;

            byte[] bytes = new byte[getPayloadSize()];

            byte[] memberData;

            // = name.getBytes();        		
            memberData = interfacetype.getBytes();

            for (int i = 0; i < (memberData.length); i++) {
                bytes[(offset + i)] = memberData[i];
            }

            offset += memberData.length;
            // = name.getBytes();        		
            char[] ssidchars = ssid.toCharArray();
            //byte[] ssidBytes = new byte[ssidchars.length];
            byte[] ssidBytes = new byte[32];

            for (int i = 0; i < 32; i++) {
                ssidBytes[i] = 0x00;
            }

            for (int i = 0; i < ssidchars.length; i++) {
                ssidBytes[i] = (byte) ssidchars[i];
            }

            memberData = ssidBytes;

            for (int i = 0; i < (memberData.length); i++) {
                bytes[(offset + i)] = memberData[i];
            }

            offset += memberData.length;
            // = name.getBytes();        		
            char[] passchars = pass.toCharArray();
            //byte[] passBytes = new byte[passchars.length];
            byte[] passBytes = new byte[64];

            for (int i = 0; i < 64; i++) {
                passBytes[i] = 0x00;
            }

            for (int i = 0; i < passchars.length; i++) {
                passBytes[i] = (byte) passchars[i];
            }

            memberData = passBytes;

            for (int i = 0; i < (memberData.length); i++) {
                bytes[(offset + i)] = memberData[i];
            }

            offset += memberData.length;
            // = name.getBytes();        		
            memberData = security.getBytes();

            for (int i = 0; i < (memberData.length); i++) {
                bytes[(offset + i)] = memberData[i];
            }

            offset += memberData.length;

            return bytes;
        }

        public static int getPayloadSize() {
            return PAYLOAD_SIZE;
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Lx::Protocol::Wifi::StateAccessPoint 
    ////////////////////////////////////////////////////////////////////////////
    public static class StateAccessPoint extends LxProtocolTypeBase { 
        // Fields: interface, ssid, security, strength, channel;

        private UInt8 interfacetype;			// Field: interfacetype - Structle::Uint8 byte offset: 0
        private String ssid;			// Field: ssid - Structle::String byte offset: 1
        private UInt8 security;			// Field: security - Structle::Uint8 byte offset: 33
        private Int16 strength;				// Field: strength - Structle::Int16 byte offset: 34
        private UInt16 channel;			// Field: channel - Structle::Uint16 byte offset: 36

        private static final int PAYLOAD_SIZE = 38;

        public StateAccessPoint(byte[] bytes) {
            this(bytes, 0);
        }

        public StateAccessPoint(byte[] bytes, int initialOffset) {
            if(bytes.length != 36 + PAYLOAD_SIZE) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, 
                        String.format("payload has more data than advertised: %s", StructleTypes.bytesToString(bytes)));
            }                                    
            
            byte[] member0Data = new byte[1];
            member0Data[0] = bytes[initialOffset + 0];

            interfacetype = new UInt8(member0Data);

            byte[] member1Data = new byte[32];
            member1Data[0] = bytes[initialOffset + 1];
            member1Data[1] = bytes[initialOffset + 2];
            member1Data[2] = bytes[initialOffset + 3];
            member1Data[3] = bytes[initialOffset + 4];
            member1Data[4] = bytes[initialOffset + 5];
            member1Data[5] = bytes[initialOffset + 6];
            member1Data[6] = bytes[initialOffset + 7];
            member1Data[7] = bytes[initialOffset + 8];
            member1Data[8] = bytes[initialOffset + 9];
            member1Data[9] = bytes[initialOffset + 10];
            member1Data[10] = bytes[initialOffset + 11];
            member1Data[11] = bytes[initialOffset + 12];
            member1Data[12] = bytes[initialOffset + 13];
            member1Data[13] = bytes[initialOffset + 14];
            member1Data[14] = bytes[initialOffset + 15];
            member1Data[15] = bytes[initialOffset + 16];
            member1Data[16] = bytes[initialOffset + 17];
            member1Data[17] = bytes[initialOffset + 18];
            member1Data[18] = bytes[initialOffset + 19];
            member1Data[19] = bytes[initialOffset + 20];
            member1Data[20] = bytes[initialOffset + 21];
            member1Data[21] = bytes[initialOffset + 22];
            member1Data[22] = bytes[initialOffset + 23];
            member1Data[23] = bytes[initialOffset + 24];
            member1Data[24] = bytes[initialOffset + 25];
            member1Data[25] = bytes[initialOffset + 26];
            member1Data[26] = bytes[initialOffset + 27];
            member1Data[27] = bytes[initialOffset + 28];
            member1Data[28] = bytes[initialOffset + 29];
            member1Data[29] = bytes[initialOffset + 30];
            member1Data[30] = bytes[initialOffset + 31];
            member1Data[31] = bytes[initialOffset + 32];

            int endOfStringIndex;
            byte[] subString;

            endOfStringIndex = member1Data.length;

            for (int i = 0; i < member1Data.length; i++) {
                if (member1Data[i] == 0x00) {
                    endOfStringIndex = i;
                    break;
                }
            }

            subString = new byte[endOfStringIndex];
            for (int i = 0; i < endOfStringIndex; i++) {
                subString[i] = member1Data[i];
            }

            ssid = new String(subString);

            byte[] member2Data = new byte[1];
            member2Data[0] = bytes[initialOffset + 33];

            security = new UInt8(member2Data);

            byte[] member3Data = new byte[2];
            member3Data[0] = bytes[initialOffset + 34];
            member3Data[1] = bytes[initialOffset + 35];

            strength = new Int16(member3Data);

            byte[] member4Data = new byte[2];
            member4Data[0] = bytes[initialOffset + 36];
            member4Data[1] = bytes[initialOffset + 37];

            channel = new UInt16(member4Data);

        }

        public StateAccessPoint(Object padding, UInt8 interfacetype, String ssid, UInt8 security, Int16 strength, UInt16 channel) {
            this.interfacetype = interfacetype;
            this.ssid = ssid;
            this.security = security;
            this.strength = strength;
            this.channel = channel;
        }

        public UInt8 getInterfacetype() {
            return interfacetype;
        }

        public String getSsid() {
            return ssid;
        }

        public UInt8 getSecurity() {
            return security;
        }

        public Int16 getStrength() {
            return strength;
        }

        public UInt16 getChannel() {
            return channel;
        }

        @Override
        public void printMessageData() {
            interfacetype.printValue("interfacetype");			// Field: interfacetype - Structle::Uint8 byte offset: 38
            //System.out.println( ssid);			// Field: ssid - Structle::String byte offset: 38
            Logger.getLogger(StateAccessPoint.class.getName()).log(Level.FINE, ssid);
            security.printValue("security");			// Field: security - Structle::Uint8 byte offset: 38
            strength.printValue("strength");				// Field: strength - Structle::Int16 byte offset: 38
            channel.printValue("channel");			// Field: channel - Structle::Uint16 byte offset: 38
        }

        public static void loadMessageDataWithPayloadAtOffset(byte[] messageData, int offset, UInt8 interfacetype, String ssid, UInt8 security, Int16 strength, UInt16 channel) {
            byte[] memberData;		// = name.getBytes();

            memberData = interfacetype.getBytes();

            for (int i = 0; i < (memberData.length); i++) {
                messageData[(offset + i)] = memberData[i];
            }

            offset += memberData.length;

            char[] ssidchars = ssid.toCharArray();
            //byte[] ssidBytes = new byte[ssidchars.length];
            byte[] ssidBytes = new byte[32];

            for (int i = 0; i < 32; i++) {
                ssidBytes[i] = 0x00;
            }

            for (int i = 0; i < ssidchars.length; i++) {
                ssidBytes[i] = (byte) ssidchars[i];
            }

            memberData = ssidBytes;

            for (int i = 0; i < (memberData.length); i++) {
                messageData[(offset + i)] = memberData[i];
            }

            offset += memberData.length;

            memberData = security.getBytes();

            for (int i = 0; i < (memberData.length); i++) {
                messageData[(offset + i)] = memberData[i];
            }

            offset += memberData.length;

            memberData = strength.getBytes();

            for (int i = 0; i < (memberData.length); i++) {
                messageData[(offset + i)] = memberData[i];
            }

            offset += memberData.length;

            memberData = channel.getBytes();

            for (int i = 0; i < (memberData.length); i++) {
                messageData[(offset + i)] = memberData[i];
            }

            offset += memberData.length;
        }

        public static void loadMessageDataWithPayloadAtDefaultOffset(byte[] messageData, UInt8 interfacetype, String ssid, UInt8 security, Int16 strength, UInt16 channel) {
            int offset = PAYLOAD_OFFSET;

            loadMessageDataWithPayloadAtOffset(messageData, offset, interfacetype, ssid, security, strength, channel);
        }

        @Override
        public byte[] getBytes() {
            int offset = 0;

            byte[] bytes = new byte[getPayloadSize()];

            byte[] memberData;

            // = name.getBytes();        		
            memberData = interfacetype.getBytes();

            for (int i = 0; i < (memberData.length); i++) {
                bytes[(offset + i)] = memberData[i];
            }

            offset += memberData.length;
            // = name.getBytes();        		
            char[] ssidchars = ssid.toCharArray();
            //byte[] ssidBytes = new byte[ssidchars.length];
            byte[] ssidBytes = new byte[32];

            for (int i = 0; i < 32; i++) {
                ssidBytes[i] = 0x00;
            }

            for (int i = 0; i < ssidchars.length; i++) {
                ssidBytes[i] = (byte) ssidchars[i];
            }

            memberData = ssidBytes;

            for (int i = 0; i < (memberData.length); i++) {
                bytes[(offset + i)] = memberData[i];
            }

            offset += memberData.length;
            // = name.getBytes();        		
            memberData = security.getBytes();

            for (int i = 0; i < (memberData.length); i++) {
                bytes[(offset + i)] = memberData[i];
            }

            offset += memberData.length;
            // = name.getBytes();        		
            memberData = strength.getBytes();

            for (int i = 0; i < (memberData.length); i++) {
                bytes[(offset + i)] = memberData[i];
            }

            offset += memberData.length;
            // = name.getBytes();        		
            memberData = channel.getBytes();

            for (int i = 0; i < (memberData.length); i++) {
                bytes[(offset + i)] = memberData[i];
            }

            offset += memberData.length;

            return bytes;
        }

        public static int getPayloadSize() {
            return PAYLOAD_SIZE;
        }
    }
}
