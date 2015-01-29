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
package com.github.besherman.lifx.impl.light;

import com.github.besherman.lifx.LFXAlarmCollection;
import com.github.besherman.lifx.LFXLight;
import com.github.besherman.lifx.LFXHSBKColor;
import com.github.besherman.lifx.LFXLightDetails;
import com.github.besherman.lifx.impl.entities.LFXPowerState;
import com.github.besherman.lifx.impl.entities.internal.LFXBinaryTypes;
import com.github.besherman.lifx.impl.entities.internal.LFXDeviceID;
import com.github.besherman.lifx.impl.entities.internal.LFXMessage;
import com.github.besherman.lifx.impl.entities.internal.LFXTarget;
import com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol;
import com.github.besherman.lifx.impl.entities.internal.structle.LxProtocolDevice;
import com.github.besherman.lifx.impl.entities.internal.structle.LxProtocolLight;
import com.github.besherman.lifx.impl.entities.internal.structle.StructleTypes;
import com.github.besherman.lifx.impl.network.LFXMessageRouter;
import com.github.besherman.lifx.impl.network.LFXTimerQueue;
import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class LFXLightImpl implements LFXLight {
    private final LFXMessageRouter router;    
    private final LFXDeviceID deviceID;    
    private final LFXTarget target;    
    private final LFXLightDetailsImpl details;
    private final LFXAlarmCollectionImpl alarms;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);    
    private final int lightLostTimeout;
    
    private boolean enabled;
    private String label = "";
    private LFXHSBKColor color;
    private Date time;        
    private long lastSeenTimestamp;

    // the messages we wait for unit we call this light loaded
    private final Set<LxProtocol.Type> messagesUntilLoaded = Collections.synchronizedSet(new HashSet<>(Arrays.asList(
            // wait for the basic information is loaded
            LxProtocol.Type.LX_PROTOCOL_LIGHT_STATE,
            LxProtocol.Type.LX_PROTOCOL_DEVICE_STATE_LABEL,
            LxProtocol.Type.LX_PROTOCOL_DEVICE_STATE_POWER,
            LxProtocol.Type.LX_PROTOCOL_DEVICE_STATE_TIME
    )));
    
    public LFXLightImpl(LFXMessageRouter router, LFXTimerQueue timerQueue, LFXDeviceID deviceID) {
        this.target = new LFXTarget(deviceID);
        this.deviceID = deviceID;
        this.router = router;        
        this.alarms = new LFXAlarmCollectionImpl(router, new LFXTarget(deviceID));
        this.details = new LFXLightDetailsImpl(router, new LFXTarget(deviceID));        
        this.lightLostTimeout = LFXLightConstants.getLightLostTimeout();
        this.lastSeenTimestamp = System.currentTimeMillis();        
    }

    @Override
    public String getID() {
        return deviceID.getStringRepresentation();
    }
    
    public LFXDeviceID getDeviceID() {
        return deviceID;
    }
    
    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public boolean isLabelAllowed(String newLabel) {
        if(newLabel == null) {
            return false;
        }
        try {
            return newLabel.getBytes("UTF-8").length <= 32;
        } catch(UnsupportedEncodingException ex) {
            throw new InternalError();
        }
    }       

    @Override
    public void setLabel(String label) {
        if(!isLabelAllowed(label)) {
            throw new IllegalArgumentException("invalid label");
        }
        
        char[] arr = label.toCharArray();
        label = new String(arr, 0, Math.min(arr.length, 32));
        
        LxProtocolDevice.SetLabel payload = new LxProtocolDevice.SetLabel(label);
        LFXMessage message = new LFXMessage(LxProtocol.Type.LX_PROTOCOL_DEVICE_SET_LABEL, target, payload);                
        for(int i = 0; i < 3; i++) {
            router.sendMessage(message);
        }        
        
        labelDidChangeTo(label);
    }

    @Override
    public LFXHSBKColor getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        setColor(new LFXHSBKColor(color));
    }

    @Override
    public void setColor(Color color, long duration) {
        setColor(new LFXHSBKColor(color), duration);
    }

    @Override
    public void setColor(LFXHSBKColor color) {
        setColor(color, 250);
    }    
    
    @Override
    public void setColor(LFXHSBKColor color, long duration) {
        if(color == null) {
            throw new IllegalArgumentException("color can not be null");
        }
        
        StructleTypes.UInt8 stream = new StructleTypes.UInt8(0);
        LxProtocolLight.Hsbk protocolColor = LFXBinaryTypes.getLXProtocolLightHsbkFromLFXHSBKColor(color);
        StructleTypes.UInt32 protocolDuration = new StructleTypes.UInt32(duration);
        LxProtocolLight.Set payload = new LxProtocolLight.Set(stream, protocolColor, protocolDuration);
        LFXMessage message = new LFXMessage(LxProtocol.Type.LX_PROTOCOL_LIGHT_SET, target, payload);        
        
        router.sendMessage(message);
        
        colorDidChangeTo(color);        
    }    

    @Override
    public float getBrightness() {
        return color != null ? color.getBrightness() : 0;
    }

    @Override
    public void setBrightness(float brightness) {
        if(color != null) {
            setColor(new LFXHSBKColor(color.getHue(), color.getSaturation(), brightness, color.getKelvin()));            
        }
    }

    @Override
    public void setBrightness(float brightness, long duration) {
        if(color != null) {
            setColor(new LFXHSBKColor(color.getHue(), color.getSaturation(), brightness, color.getKelvin()), duration);            
        }
    }
    
    

    @Override
    public boolean isPower() {
        return enabled;
    }

    @Override
    public void setPower(boolean power) {
        LFXPowerState state = power ? LFXPowerState.ON : LFXPowerState.OFF;
        StructleTypes.UInt16 protocolPowerLevel = LFXBinaryTypes.getLFXProtocolPowerLevelFromLFXPowerState(state);
        LxProtocolDevice.SetPower payload = new LxProtocolDevice.SetPower(protocolPowerLevel);
        LFXMessage message = new LFXMessage(LxProtocol.Type.LX_PROTOCOL_DEVICE_SET_POWER, target, payload);
        
        router.sendMessage(message);
        router.sendMessage(message);
        
        powerDidChangeTo(state);
    }

    @Override
    public LFXAlarmCollection getAlarms() {
        return alarms;
    }

    @Override
    public Date getTime() {
        return new Date();
    }

    @Override
    public LFXLightDetails getDetails() {
        return details;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    @Override
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener l) {
        pcs.addPropertyChangeListener(propertyName, l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }    
    
    public boolean isLoaded() {
        return messagesUntilLoaded.isEmpty() && alarms.isLoaded() && details.isLoaded();
    }
    
    /**
     * Returns the message types this light is waiting for before it can call
     * itself loaded. 
     * 
     * TODO: remove this
     */
    public Set<LxProtocol.Type> getMessagesUntilLoaded() {
        return this.messagesUntilLoaded;
    }
        
    public LFXTarget getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return "LFXLightImpl{" + "deviceID=" + deviceID + '}';
    }
    
    public boolean isLost() {
        return (System.currentTimeMillis() - lastSeenTimestamp) > lightLostTimeout;
    }
    
    public void close() {
        details.close();
        alarms.close();
    }
    
    public void handleMessage(LFXMessage message) {
        lastSeenTimestamp = System.currentTimeMillis();
        switch (message.getType()) {
            case LX_PROTOCOL_LIGHT_STATE: {
                LxProtocolLight.State payload = (LxProtocolLight.State) message.getPayload();
                labelDidChangeTo(payload.getLabel());
                colorDidChangeTo(LFXBinaryTypes.getLFXHSBKColorFromLXProtocolLightHsbk(payload.getColor()));
                powerDidChangeTo(LFXBinaryTypes.getLFXPowerStateFromLFXProtocolPowerLevel(payload.getPower()));
                break;
            }
            case LX_PROTOCOL_DEVICE_STATE_LABEL: {
                LxProtocolDevice.StateLabel payload = (LxProtocolDevice.StateLabel) message.getPayload();
                labelDidChangeTo(payload.getLabel());
                break;
            }
            case LX_PROTOCOL_DEVICE_STATE_POWER: {
                LxProtocolDevice.StatePower payload = (LxProtocolDevice.StatePower) message.getPayload();
                powerDidChangeTo(LFXBinaryTypes.getLFXPowerStateFromLFXProtocolPowerLevel(payload.getLevel()));
                break;
            }
            case LX_PROTOCOL_DEVICE_STATE_TIME: {
                LxProtocolDevice.StateTime payload = message.getPayload();
                timeDidChangeTo(payload.getTime().getBigIntegerValue());
                break;
            }
            case LX_PROTOCOL_DEVICE_STATE_INFO: {
                // The light handler does not send this automatically, but when
                // the LFXLightDetailsImpl sends it we can read it 
                LxProtocolDevice.StateInfo payload = message.getPayload();                                
                timeDidChangeTo(payload.getTime().getBigIntegerValue());
                break;
            }
            default:
                break;
        }
        
        alarms.handleMessage(message);
        details.handleMessage(message);
        
        messagesUntilLoaded.remove(message.getType());
    }    
    
    private void labelDidChangeTo(String newLabel) {
        String oldLabel = label;
        label = newLabel;
        pcs.firePropertyChange("label", oldLabel, newLabel);
    }

    public void colorDidChangeTo(LFXHSBKColor newColor) {
        LFXHSBKColor oldColor = color;
        color = newColor;
        pcs.firePropertyChange("color", oldColor, newColor);        
    }

    public void powerDidChangeTo(LFXPowerState powerState) {
        boolean oldValue = enabled;
        this.enabled = (powerState == powerState.ON);
        pcs.firePropertyChange("power", oldValue, enabled);
    }
    
    private void timeDidChangeTo(BigInteger newTime) {
        Date oldTime = time;
        long msSinceEpoch = newTime.longValue() / 1000000;
        time = new Date(msSinceEpoch);        
        pcs.firePropertyChange("time", oldTime, time);
    }    
}
