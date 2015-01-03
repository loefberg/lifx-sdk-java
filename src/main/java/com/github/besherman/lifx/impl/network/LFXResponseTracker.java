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

import com.github.besherman.lifx.impl.entities.internal.LFXBinaryPath;
import com.github.besherman.lifx.impl.entities.internal.LFXBinaryTargetID;
import com.github.besherman.lifx.impl.entities.internal.LFXBinaryTargetType;
import com.github.besherman.lifx.impl.entities.internal.LFXDeviceID;
import com.github.besherman.lifx.impl.entities.internal.LFXMessage;
import com.github.besherman.lifx.impl.entities.internal.LFXSiteID;
import com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol;
import com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_DEVICE_GET_DUMMY_LOAD;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_DEVICE_GET_INFO;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_DEVICE_GET_LABEL;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_DEVICE_GET_MCU_RAIL_VOLTAGE;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_DEVICE_GET_MESH_FIRMWARE;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_DEVICE_GET_MESH_INFO;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_DEVICE_GET_PAN_GATEWAY;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_DEVICE_GET_POWER;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_DEVICE_GET_RESET_SWITCH;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_DEVICE_GET_TAGS;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_DEVICE_GET_TIME;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_DEVICE_GET_VERSION;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_DEVICE_GET_WIFI_FIRMWARE;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_DEVICE_GET_WIFI_INFO;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_DEVICE_STATE_DUMMY_LOAD;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_DEVICE_STATE_INFO;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_DEVICE_STATE_LABEL;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_DEVICE_STATE_MCU_RAIL_VOLTAGE;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_DEVICE_STATE_MESH_FIRMWARE;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_DEVICE_STATE_MESH_INFO;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_DEVICE_STATE_PAN_GATEWAY;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_DEVICE_STATE_POWER;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_DEVICE_STATE_RESET_SWITCH;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_DEVICE_STATE_TAGS;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_DEVICE_STATE_TIME;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_DEVICE_STATE_VERSION;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_DEVICE_STATE_WIFI_FIRMWARE;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_DEVICE_STATE_WIFI_INFO;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_LIGHT_GET;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_LIGHT_GET_POWER;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_LIGHT_GET_RAIL_VOLTAGE;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_LIGHT_GET_TEMPERATURE;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_LIGHT_STATE;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_LIGHT_STATE_POWER;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_LIGHT_STATE_RAIL_VOLTAGE;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_LIGHT_STATE_TEMPERATURE;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_SENSOR_GET_AMBIENT_LIGHT;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_SENSOR_GET_DIMMER_VOLTAGE;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_SENSOR_STATE_AMBIENT_LIGHT;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_SENSOR_STATE_DIMMER_VOLTAGE;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_WIFI_GET;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_WIFI_GET_ACCESS_POINT;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_WIFI_STATE;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_WIFI_STATE_ACCESS_POINT;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Keeps track of sent messages and if there is no response for a while the
 * same message is sent again.
 */
public class LFXResponseTracker {
    private LFXTimerQueue timerQueue;
    private BlockingQueue<LFXSocketMessage> outgoingQueue;
    private final Map<LxProtocol.Type, LxProtocol.Type> reqResp = createReqResp();
    private final Object lock = new Object();
    private final PriorityQueue<Expected> expectedResponses = new PriorityQueue<>();
    private final LFXRoutingTable routingTable;
    
    private int messageSendRateLimitInterval;
    private int responseTrackerInterval;
    private int responseTrackerTimeout;
    
    public LFXResponseTracker(LFXRoutingTable routingTable) {
        this.routingTable = routingTable;
    }
    
    public void setOutgoingQueue(BlockingQueue<LFXSocketMessage> outgoingQueue) {
        this.outgoingQueue = outgoingQueue;
    }    
    
    public void open() {
        timerQueue = new LFXTimerQueue();                
        responseTrackerTimeout = LFXConstants.getResponseTrackerResendTimeout();        
        responseTrackerInterval = LFXConstants.getResponseTrackerInterval();        
        messageSendRateLimitInterval = LFXConstants.getNetworkLoopSendRateLimitInterval();
        
        timerQueue.doRepeatedly(updateTimeoutsRunnable, responseTrackerInterval, TimeUnit.MILLISECONDS);
    }
    
    public void close() {
        if(timerQueue != null) {
            timerQueue.close();
        }
    }
    
    public void trackResponse(LFXMessage message, LFXSocketMessage sm) {
        LFXBinaryPath path = message.getPath();
        LFXSiteID site = path.getSiteID();
        if(site.isZeroSite()) {
            return;
        }
        LFXBinaryTargetID target = path.getBinaryTargetID();
        if(target.geTargetType() != LFXBinaryTargetType.DEVICE) {
            return;
        }
        
        Type expectedResponse = reqResp.get(message.getType());
        if(expectedResponse != null) {
            synchronized(lock) {
                expectedResponses.add(new Expected(expectedResponse, target.getDeviceID(), getTimeout(), sm));
            }
        }        
    }
    

    public void updateReponse(LFXMessage message) {
        synchronized(lock) {
            Iterator<Expected> it = expectedResponses.iterator();
            while(it.hasNext()) {
                Expected ex = it.next();
                if(ex.isResponse(message)) {
                    it.remove();                    
                }
            }
        }
    }
    
    private long getTimeout() {
        long timeoutAfter = System.currentTimeMillis() + responseTrackerTimeout;

        // the outgoing message queue might be long, so we have to take
        // take that into account as well            
        timeoutAfter +=  outgoingQueue.size() * messageSendRateLimitInterval;
        
        return timeoutAfter;
    }
    
    
    private void updateTimeouts() {
        synchronized(lock) {
            while(!expectedResponses.isEmpty() && expectedResponses.peek().isTimedOut()) {                
                Expected ex = expectedResponses.poll();
                if(routingTable.isLightStillAlive(ex.getDeviceID())) {                    
                    ex.reschedule(getTimeout());
                    Logger.getLogger(LFXResponseTracker.class.getName()).log(Level.FINE, "Resending " + ex.type);
                    if(!outgoingQueue.offer(ex.getMessage())) {
                        Logger.getLogger(LFXResponseTracker.class.getName()).log(Level.SEVERE, 
                                "Failed to send message, queue is full");
                    }
                    expectedResponses.add(ex);
                }
            }
        }
    }

    private final Runnable updateTimeoutsRunnable = new Runnable() {
        @Override public void run() {
            updateTimeouts();
        }
    };


    
    private static Map<LxProtocol.Type, LxProtocol.Type> createReqResp() {
        Map<LxProtocol.Type, LxProtocol.Type> map = new HashMap<>();
        map.put(LX_PROTOCOL_DEVICE_GET_PAN_GATEWAY, LX_PROTOCOL_DEVICE_STATE_PAN_GATEWAY);
        map.put(LX_PROTOCOL_DEVICE_GET_TIME, LX_PROTOCOL_DEVICE_STATE_TIME);
        map.put(LX_PROTOCOL_DEVICE_GET_RESET_SWITCH, LX_PROTOCOL_DEVICE_STATE_RESET_SWITCH);
        map.put(LX_PROTOCOL_DEVICE_GET_DUMMY_LOAD, LX_PROTOCOL_DEVICE_STATE_DUMMY_LOAD);
        map.put(LX_PROTOCOL_DEVICE_GET_MESH_INFO, LX_PROTOCOL_DEVICE_STATE_MESH_INFO);
        map.put(LX_PROTOCOL_DEVICE_GET_MESH_FIRMWARE, LX_PROTOCOL_DEVICE_STATE_MESH_FIRMWARE);
        map.put(LX_PROTOCOL_DEVICE_GET_WIFI_INFO, LX_PROTOCOL_DEVICE_STATE_WIFI_INFO);
        map.put(LX_PROTOCOL_DEVICE_GET_WIFI_FIRMWARE, LX_PROTOCOL_DEVICE_STATE_WIFI_FIRMWARE);
        map.put(LX_PROTOCOL_DEVICE_GET_POWER, LX_PROTOCOL_DEVICE_STATE_POWER);
        map.put(LX_PROTOCOL_DEVICE_GET_LABEL, LX_PROTOCOL_DEVICE_STATE_LABEL);
        map.put(LX_PROTOCOL_DEVICE_GET_TAGS, LX_PROTOCOL_DEVICE_STATE_TAGS);         
        // map.put(LX_PROTOCOL_DEVICE_GET_TAG_LABELS, LX_PROTOCOL_DEVICE_STATE_TAG_LABELS); // takes arguments
        map.put(LX_PROTOCOL_DEVICE_GET_VERSION, LX_PROTOCOL_DEVICE_STATE_VERSION);
        map.put(LX_PROTOCOL_DEVICE_GET_INFO, LX_PROTOCOL_DEVICE_STATE_INFO);
        map.put(LX_PROTOCOL_DEVICE_GET_MCU_RAIL_VOLTAGE, LX_PROTOCOL_DEVICE_STATE_MCU_RAIL_VOLTAGE);
        map.put(LX_PROTOCOL_LIGHT_GET, LX_PROTOCOL_LIGHT_STATE);
        map.put(LX_PROTOCOL_LIGHT_GET_RAIL_VOLTAGE, LX_PROTOCOL_LIGHT_STATE_RAIL_VOLTAGE);
        map.put(LX_PROTOCOL_LIGHT_GET_TEMPERATURE, LX_PROTOCOL_LIGHT_STATE_TEMPERATURE);
        //map.put(LX_PROTOCOL_LIGHT_GET_SIMPLE_EVENT, LX_PROTOCOL_LIGHT_STATE_SIMPLE_EVENT); // takes arguments
        map.put(LX_PROTOCOL_LIGHT_GET_POWER, LX_PROTOCOL_LIGHT_STATE_POWER);
        map.put(LX_PROTOCOL_WIFI_GET, LX_PROTOCOL_WIFI_STATE);
        map.put(LX_PROTOCOL_WIFI_GET_ACCESS_POINT, LX_PROTOCOL_WIFI_STATE_ACCESS_POINT);
        map.put(LX_PROTOCOL_SENSOR_GET_AMBIENT_LIGHT, LX_PROTOCOL_SENSOR_STATE_AMBIENT_LIGHT);
        map.put(LX_PROTOCOL_SENSOR_GET_DIMMER_VOLTAGE, LX_PROTOCOL_SENSOR_STATE_DIMMER_VOLTAGE);
        return map;        
    }
    
    private static class Expected implements Comparable<Expected> {
        private final Type type;
        private final LFXDeviceID device;
        private long timeoutAfterTimestamp;
        private final LFXSocketMessage originalSocketMessage;

        public Expected(Type type, LFXDeviceID device, long timeoutAfter, LFXSocketMessage message) {
            this.type = type;
            this.device = device;
            this.timeoutAfterTimestamp = timeoutAfter;
            this.originalSocketMessage = message;
        }

        @Override
        public int compareTo(Expected other) {
            return Long.compare(timeoutAfterTimestamp, other.timeoutAfterTimestamp);
        }
        
        public boolean isTimedOut() {
            return System.currentTimeMillis() > timeoutAfterTimestamp;
        }
        
        public void reschedule(long newTimeout) {
            this.timeoutAfterTimestamp = newTimeout;
        }
        
        public LFXSocketMessage getMessage() {
            return originalSocketMessage;
        }      
        
        public LFXDeviceID getDeviceID() {
            return device;
        }
        
        public boolean isResponse(LFXMessage message) {
            if(message.getType().equals(type)) {
                LFXBinaryTargetID target = message.getPath().getBinaryTargetID();
                if(target.geTargetType() == LFXBinaryTargetType.DEVICE) {
                    if(this.device.equals(target.getDeviceID())) {
                        return true;
                    } 
                }
            }
            
            return false;
        }

        @Override
        public String toString() {
            return String.format("%s %s", type, device.getStringRepresentation());
        }
    }
}
