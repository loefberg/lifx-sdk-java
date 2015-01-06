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

import com.github.besherman.lifx.LFXAlarm;
import com.github.besherman.lifx.LFXAlarmCollection;
import com.github.besherman.lifx.LFXHSBKColor;
import com.github.besherman.lifx.LFXInterfaceFirmware;
import com.github.besherman.lifx.LFXWaveform;
import com.github.besherman.lifx.impl.entities.internal.LFXBinaryTypes;
import com.github.besherman.lifx.impl.entities.internal.LFXByteUtils;
import com.github.besherman.lifx.impl.entities.internal.LFXMessage;
import com.github.besherman.lifx.impl.entities.internal.LFXTarget;
import com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_DEVICE_STATE_WIFI_FIRMWARE;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_LIGHT_SET_SIMPLE_EVENT;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_LIGHT_STATE_SIMPLE_EVENT;
import com.github.besherman.lifx.impl.entities.internal.structle.LxProtocolDevice;
import com.github.besherman.lifx.impl.entities.internal.structle.LxProtocolLight;
import com.github.besherman.lifx.impl.entities.internal.structle.StructleTypes;
import com.github.besherman.lifx.impl.network.LFXMessageRouter;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The protocol specifies 256 possible alarms, but it also has a max flag that
 * is sent with each message which tells us how many alarms the light can 
 * use. 
 */
public class LFXAlarmCollectionImpl implements LFXAlarmCollection {
    private final LFXTarget target;
    private final LFXMessageRouter router;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    private final List<LFXAlarm> alarms = Collections.synchronizedList(new ArrayList<LFXAlarm>());    
    
    private final AtomicBoolean closed = new AtomicBoolean(false);
    
    private final AtomicBoolean clearForInit = new AtomicBoolean(false);
    
    
    public LFXAlarmCollectionImpl(LFXMessageRouter router, LFXTarget target) {
        this.target = target;
        this.router = router;        
    }

    @Override
    public int size() {
        return alarms.size();
    }

    @Override
    public Iterator<LFXAlarm> iterator() {
        return Collections.unmodifiableList(alarms).iterator();
    }

    @Override
    public LFXAlarm get(int index) {
        synchronized(alarms) {
            if(index < 0 || index >= size()) {
                throw new IndexOutOfBoundsException();
            }
            return alarms.get(index);
        }
    }

    @Override
    public void set(int index, LFXAlarm alarm) {
        LFXAlarm old;
        synchronized(alarms) {
            if(index < 0 || index >= size()) {
                throw new IndexOutOfBoundsException();
            }

            old = alarms.get(index);
            alarms.set(index, alarm);
        }
        
        pcs.fireIndexedPropertyChange("alarms", index, old, alarm);        
        sendSetAlarm(index, alarm);
    }

    @Override
    public void clear(int index) {
        LFXAlarm old;
        LFXAlarm cleared = createEmptyAlarm();
        synchronized(alarms) {
            if(index < 0 || index >= size()) {
                throw new IndexOutOfBoundsException();
            }
            
            old = alarms.get(index);
            alarms.set(index, cleared);
        }
        
        pcs.fireIndexedPropertyChange("alarms", index, old, cleared);        
        sendSetAlarm(index, cleared);
    }
    

    @Override
    public void load() {
        if(closed.get()) {            
            return;
        }
        
        if(!clearForInit.get()) {
            return;
        }                

        // TODO: this should be revisited when lights supports more than two
        // alarms.
        sendGetAlarm(0);        
        sendGetAlarm(1);        
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
    
    public void close() {        
        closed.set(false);
    }    
    
    public boolean isLoaded() {
        // TODO implement this
        return true;
    }    

    public void handleMessage(LFXMessage message) {
        if(message.getType() == LX_PROTOCOL_LIGHT_STATE_SIMPLE_EVENT) {
            LxProtocolLight.StateSimpleEvent payload = message.getPayload();
            String hex = LFXByteUtils.byteArrayToHexString(payload.getBytes());
            
            simpleEventDidChangeTo(payload);
        } else if(message.getType() == LX_PROTOCOL_DEVICE_STATE_WIFI_FIRMWARE) {
            if(!clearForInit.get()) {
                LxProtocolDevice.StateWifiFirmware payload = message.getPayload();
                LFXInterfaceFirmware wifi = LFXBinaryTypes.createFirmware(payload.getBuild(), payload.getInstall(), payload.getVersion());
                boolean lightSupportsAlarms = wifi.getMajorVersion() > 1 || wifi.getMajorVersion() == 1 && wifi.getMinorVersion() >= 5;
                if(lightSupportsAlarms) {
                    clearForInit.set(true);
                    load();
                } else {
                    Logger.getLogger(LFXAlarmCollectionImpl.class.getName()).log(Level.INFO, 
                            String.format("light %s has firmware version %s which does not support alarms", 
                                    target.getDeviceID().getStringRepresentation(),
                                    wifi.getVersion()));
                }
            }
        }
    }
    
    
    private static LFXAlarm createEmptyAlarm() {
        LFXHSBKColor emptyColor = new LFXHSBKColor(0, 0, 0, 0);
        LFXWaveform emptyWaveform = new LFXWaveform(0, false, emptyColor, 0, 0, (short)0, 0);
        Date emptyDate = new Date(0);        
        return new LFXAlarm(emptyDate, false, 0, emptyWaveform);        
    }    
    
    private void sendSetAlarm(int index, LFXAlarm alarm) {
        LxProtocolLight.Waveform waveform;
        {
            StructleTypes.UInt8 stream = new StructleTypes.UInt8(alarm.getWaveform().getStream());
            StructleTypes.Bool8 transienttype = new StructleTypes.Bool8(alarm.getWaveform().isTransientType());
            LxProtocolLight.Hsbk color = LFXBinaryTypes.getLXProtocolLightHsbkFromLFXHSBKColor(alarm.getWaveform().getColor());
            StructleTypes.UInt32 period = new StructleTypes.UInt32(alarm.getWaveform().getPeriod());
            StructleTypes.Float32 cycles = new StructleTypes.Float32(alarm.getWaveform().getCycles());
            StructleTypes.Int16 skrew_ratio = new StructleTypes.Int16(alarm.getWaveform().getSkewRatio());
            StructleTypes.UInt8 wf = new StructleTypes.UInt8(alarm.getWaveform().getWaveform());            
            
            waveform = new LxProtocolLight.Waveform(stream, transienttype, color, period, cycles, skrew_ratio, wf);
        }
        
        StructleTypes.UInt8 idx = new StructleTypes.UInt8(index);
        BigInteger t = BigInteger.valueOf(alarm.getTime().getTime());
        t = t.multiply(new BigInteger("1000000"));
        StructleTypes.UInt64 time = new StructleTypes.UInt64(t);
        StructleTypes.UInt16 power = new StructleTypes.UInt16(alarm.isPower() ? 1 : 0);
        StructleTypes.UInt32 duration = new StructleTypes.UInt32(alarm.getDuration());
        LxProtocolLight.SetSimpleEvent payload = new LxProtocolLight.SetSimpleEvent(idx, time, power, duration, waveform);
        
        for(int i = 0; i < 3; i++) {
            router.sendMessage(new LFXMessage(LX_PROTOCOL_LIGHT_SET_SIMPLE_EVENT, target, payload));
        }
    }
    
    private void simpleEventDidChangeTo(LxProtocolLight.StateSimpleEvent payload) {        
        int index = payload.getIndex().getValue();
        
        synchronized(alarms) {
            int maxNumberOfAlarms = payload.getMax().getValue();
            while(maxNumberOfAlarms > alarms.size()) {
                alarms.add(createEmptyAlarm());
            }
        }
        
        if(index < 0 || index >= alarms.size()) {
            Logger.getLogger(LFXAlarmCollectionImpl.class.getName()).log(Level.SEVERE, 
                    "Received alarm with index out of range. Index was {0} size is {1}", 
                    new Object[]{index, alarms.size()});
            return;
        }
        
        LxProtocolLight.Waveform wf = payload.getWaveform();        
        LFXWaveform waveform = new LFXWaveform(wf.getStream().getValue(), 
                wf.getTransienttype().getValue(), 
                LFXBinaryTypes.getLFXHSBKColorFromLXProtocolLightHsbk(wf.getColor()), 
                wf.getPeriod().getValue(), 
                wf.getCycles().getValue(), 
                (short)wf.getSkewRatio().getValue(), 
                wf.getWaveform().getValue()); 
        
        BigInteger time = payload.getTime().getBigIntegerValue();
        time = time.divide(new BigInteger("1000000"));
        Date date = new Date(time.longValue());
        
        LFXAlarm alarm = new LFXAlarm(date, 
                payload.getPower().getValue() > 0, 
                payload.getDuration().getValue(), 
                waveform);        

        
        LFXAlarm old;                
        synchronized(alarms) {
            old = alarms.get(index);
            alarms.set(index, alarm);     
        }   
         
        pcs.fireIndexedPropertyChange("alarms", index, old, alarm);        
    }   
   
    
    private void sendGetAlarm(int index) {         
        LxProtocolLight.GetSimpleEvent payload = new LxProtocolLight.GetSimpleEvent(new StructleTypes.UInt8(index));
        LFXMessage msg = new LFXMessage(LxProtocol.Type.LX_PROTOCOL_LIGHT_GET_SIMPLE_EVENT, target, payload);
        router.sendMessage(msg);
    }

}
