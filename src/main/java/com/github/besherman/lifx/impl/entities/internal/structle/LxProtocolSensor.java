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

import com.github.besherman.lifx.impl.entities.internal.structle.StructleTypes.Float32;
import com.github.besherman.lifx.impl.entities.internal.structle.StructleTypes.LxProtocolTypeBase;
import com.github.besherman.lifx.impl.entities.internal.structle.StructleTypes.UInt32;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public class LxProtocolSensor {

    ////////////////////////////////////////////////////////////////////////////
    //
    ////////////////////////////////////////////////////////////////////////////
    public static class GetAmbientLight extends LxProtocolTypeBase { // Struct: Lx::Protocol::Sensor::GetAmbientLight 
        private static final int PAYLOAD_SIZE = 0;

        public GetAmbientLight(byte[] bytes) {
            this(bytes, 0);
        }

        public GetAmbientLight(byte[] bytes, int initialOffset) {
            if(bytes.length != 36 + PAYLOAD_SIZE) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, 
                        String.format("payload has more data than advertised: %s", StructleTypes.bytesToString(bytes)));
            }                                    
            
        }

        public GetAmbientLight() {
        }

        @Override
        public void printMessageData() {
        }


        @Override
        public byte[] getBytes() {
            return new byte[0];
        }

        public static int getPayloadSize() {
            return PAYLOAD_SIZE;
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    //
    ////////////////////////////////////////////////////////////////////////////
    public static class StateAmbientLight extends LxProtocolTypeBase { // Struct: Lx::Protocol::Sensor::StateAmbientLight 
        // Fields: lux;

        private Float32 lux;				// Field: lux - Structle::Float byte offset: 0

        private static final int PAYLOAD_SIZE = 4;

        public StateAmbientLight(byte[] bytes) {            
            this(bytes, 0);
        }

        public StateAmbientLight(byte[] bytes, int initialOffset) {
            if(bytes.length != 36 + PAYLOAD_SIZE) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, 
                        String.format("payload has more data than advertised: %s", StructleTypes.bytesToString(bytes)));
            }                                    
            
            byte[] member0Data = new byte[4];
            member0Data[0] = bytes[initialOffset + 0];
            member0Data[1] = bytes[initialOffset + 1];
            member0Data[2] = bytes[initialOffset + 2];
            member0Data[3] = bytes[initialOffset + 3];

            lux = new Float32(member0Data);

        }

        public StateAmbientLight(Object padding, Float32 lux) {
            this.lux = lux;
        }

        public Float32 getLux() {
            return lux;
        }

        @Override
        public void printMessageData() {
            lux.printValue("lux");				// Field: lux - Structle::Float byte offset: 4
        }

        public static void loadMessageDataWithPayloadAtOffset(byte[] messageData, int offset, Float32 lux) {
            byte[] memberData;		// = name.getBytes();

            memberData = lux.getBytes();

            for (int i = 0; i < (memberData.length); i++) {
                messageData[(offset + i)] = memberData[i];
            }

            offset += memberData.length;
        }

        public static void loadMessageDataWithPayloadAtDefaultOffset(byte[] messageData, Float32 lux) {
            int offset = PAYLOAD_OFFSET;

            loadMessageDataWithPayloadAtOffset(messageData, offset, lux);
        }

        @Override
        public byte[] getBytes() {
            int offset = 0;

            byte[] bytes = new byte[getPayloadSize()];

            byte[] memberData;

            // = name.getBytes();        		
            memberData = lux.getBytes();

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
    //
    ////////////////////////////////////////////////////////////////////////////
    public static class GetDimmerVoltage extends LxProtocolTypeBase { // Struct: Lx::Protocol::Sensor::GetDimmerVoltage 
        private static final int PAYLOAD_SIZE = 0;

        public GetDimmerVoltage(byte[] bytes) {
            this(bytes, 0);
        }

        public GetDimmerVoltage(byte[] bytes, int initialOffset) {
            if(bytes.length != 36 + PAYLOAD_SIZE) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, 
                        String.format("payload has more data than advertised: %s", StructleTypes.bytesToString(bytes)));
            }                                    
            
        }

        public GetDimmerVoltage() {
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
    //
    ////////////////////////////////////////////////////////////////////////////
    public static class StateDimmerVoltage extends LxProtocolTypeBase { // Struct: Lx::Protocol::Sensor::StateDimmerVoltage 
        // Fields: voltage;

        private UInt32 voltage;			// Field: voltage - Structle::Uint32 byte offset: 0

        private static final int PAYLOAD_SIZE = 4;

        public StateDimmerVoltage(byte[] bytes) {
            this(bytes, 0);
        }

        public StateDimmerVoltage(byte[] bytes, int initialOffset) {
            if(bytes.length != 36 + PAYLOAD_SIZE) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, 
                        String.format("payload has more data than advertised: %s", StructleTypes.bytesToString(bytes)));
            }                                    
            
            byte[] member0Data = new byte[4];
            member0Data[0] = bytes[initialOffset + 0];
            member0Data[1] = bytes[initialOffset + 1];
            member0Data[2] = bytes[initialOffset + 2];
            member0Data[3] = bytes[initialOffset + 3];

            voltage = new UInt32(member0Data);
        }

        public StateDimmerVoltage(Object padding, UInt32 voltage) {
            this.voltage = voltage;
        }

        public UInt32 getVoltage() {
            return voltage;
        }

        @Override
        public void printMessageData() {
            voltage.printValue("voltage");			// Field: voltage - Structle::Uint32 byte offset: 4
        }

        public static void loadMessageDataWithPayloadAtOffset(byte[] messageData, int offset, UInt32 voltage) {
            byte[] memberData;		// = name.getBytes();

            memberData = voltage.getBytes();

            for (int i = 0; i < (memberData.length); i++) {
                messageData[(offset + i)] = memberData[i];
            }

            offset += memberData.length;
        }

        public static void loadMessageDataWithPayloadAtDefaultOffset(byte[] messageData, UInt32 voltage) {
            int offset = PAYLOAD_OFFSET;

            loadMessageDataWithPayloadAtOffset(messageData, offset, voltage);
        }

        @Override
        public byte[] getBytes() {
            int offset = 0;

            byte[] bytes = new byte[getPayloadSize()];

            byte[] memberData;

            // = name.getBytes();        		
            memberData = voltage.getBytes();

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