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
import com.github.besherman.lifx.LFXWaveform;
import com.github.besherman.lifx.impl.entities.internal.LFXBinaryTypes;
import com.github.besherman.lifx.impl.entities.internal.LFXMessage;
import com.github.besherman.lifx.impl.entities.internal.LFXTarget;
import com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_LIGHT_SET_SIMPLE_EVENT;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_LIGHT_STATE_SIMPLE_EVENT;
import com.github.besherman.lifx.impl.entities.internal.structle.LxProtocolLight;
import com.github.besherman.lifx.impl.entities.internal.structle.StructleTypes;
import com.github.besherman.lifx.impl.network.LFXMessageRouter;
import com.github.besherman.lifx.impl.network.LFXTimerQueue;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The protocol specifies 256 possible alarms, but it also has a max flag that
 * is sent with each message which tells us how many alarms the light can 
 * use. When load() is called we asks for the first alarm, when we get that
 * response we ask for the next alarm until we've loaded max number of alarms.
 * When load() is called we also start a timer that resends the current request
 * if it times out.
 */
public class LFXAlarmCollectionImpl implements LFXAlarmCollection {
    private final LFXTarget target;
    private final LFXMessageRouter router;
    private final LFXTimerQueue timerQueue;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    private final List<LFXAlarm> alarms = Collections.synchronizedList(new ArrayList<LFXAlarm>());    
    
    /** When the light is closed/disposed we don't want to trigger loads. */
    private final AtomicBoolean closed = new AtomicBoolean(false);
    
    /** true while we are loading */
    private boolean loading = false;
    
    /** the current index that the load process is waiting for */
    private int currentIndexRequested;
    
    /** when we asked for the last index */
    private long requestStartedTimestamp;
    
    /** the timeout trackers key, so that we can cancel it when done or closed */
    private Object timerKey;    
    
    
    public LFXAlarmCollectionImpl(LFXMessageRouter router, LFXTimerQueue timerQueue, LFXTarget target) {
        this.target = target;
        this.router = router;
        this.timerQueue = timerQueue;        
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
        
        synchronized(alarms) {
            if(loading == false) {                                                
                loading = true;                                
                currentIndexRequested = 0;
                sendRequest();
                timerKey = timerQueue.doRepeatedly(checkRefreshTimeoutAction, 10, TimeUnit.MILLISECONDS);
            } 
        }
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
        synchronized(alarms) {
            // needs to be synchronized becase we can close at the same time
            // as someone calls load()
            if(timerKey != null) {
                timerQueue.cancel(timerKey);
            }
        }
    }    

    public void handleMessage(LFXMessage message) {
        if(message.getType() == LX_PROTOCOL_LIGHT_STATE_SIMPLE_EVENT) {
            LxProtocolLight.StateSimpleEvent payload = message.getPayload();
            simpleEventDidChangeTo(payload);
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
        StructleTypes.UInt16 power = new StructleTypes.UInt16(alarm.getPower() ? 1 : 0);
        StructleTypes.UInt32 duration = new StructleTypes.UInt32(alarm.getDuration());
        LxProtocolLight.SetSimpleEvent payload = new LxProtocolLight.SetSimpleEvent(idx, time, power, duration, waveform);
        
        for(int i = 0; i < 3; i++) {
            router.sendMessage(new LFXMessage(LX_PROTOCOL_LIGHT_SET_SIMPLE_EVENT, target, payload));
        }
    }
    
    private void simpleEventDidChangeTo(LxProtocolLight.StateSimpleEvent payload) {        
        resize(payload.getMax().getValue());
        
        int index = payload.getIndex().getValue();
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
            
            
            if(loading) {
                if(currentIndexRequested == index) {                    
                    //if(!isEmpty(alarm)) {
                    if(currentIndexRequested < alarms.size() - 1) {
                        currentIndexRequested += 1;
                        sendRequest();
                    } else {       
                        loading = false;
                        timerQueue.cancel(timerKey);
                        timerKey = null;
                    }
                }
            }
        }
        pcs.fireIndexedPropertyChange("alarms", index, old, alarm);
    }
    
    private void resize(int max) {
        int old;
        synchronized(alarms) {
            old = alarms.size();
            while(max > alarms.size()) {
                alarms.add(createEmptyAlarm());
            }
        }
        pcs.firePropertyChange("size", old, alarms.size());
    }
   
    
    private void sendRequest() {         
        int index = currentIndexRequested;
        LxProtocolLight.GetSimpleEvent payload = new LxProtocolLight.GetSimpleEvent(new StructleTypes.UInt8(index));
        LFXMessage msg = new LFXMessage(LxProtocol.Type.LX_PROTOCOL_LIGHT_GET_SIMPLE_EVENT, target, payload);
        router.sendMessage(msg);
        requestStartedTimestamp = System.currentTimeMillis();
    }
    
    private final Runnable checkRefreshTimeoutAction = new Runnable() {
        @Override
        public void run() {
            synchronized(alarms) {
                if(loading) {
                    // TODO: config this
                    if((System.currentTimeMillis() - requestStartedTimestamp) > 500) {
                        sendRequest();
                    }
                }
            }
        }
    };

}
