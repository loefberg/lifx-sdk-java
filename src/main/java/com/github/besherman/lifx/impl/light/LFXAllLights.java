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

import com.github.besherman.lifx.LFXLight;
import com.github.besherman.lifx.LFXLightCollection;
import com.github.besherman.lifx.LFXLightCollectionListener;
import com.github.besherman.lifx.impl.entities.internal.LFXDeviceID;
import com.github.besherman.lifx.impl.entities.internal.LFXMessage;
import com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol;
import com.github.besherman.lifx.impl.network.LFXMessageRouter;
import com.github.besherman.lifx.impl.network.LFXTimerQueue;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Richard
 */
public class LFXAllLights implements LFXLightCollection {        
    private volatile CountDownLatch allLightsLoaded = new CountDownLatch(1);
    private final LFXLightCollectionImpl lights = new LFXLightCollectionImpl();

    @Override
    public int size() {
        return lights.size();
    }

    @Override
    public boolean isEmpty() {
        return lights.isEmpty();
    }

    @Override
    public boolean contains(LFXLight light) {
        return lights.contains(light);
    }

    @Override
    public Iterator<LFXLight> iterator() {
        return lights.iterator();
    }

    @Override
    public LFXLight getLightByLabel(String label) {
        return lights.getLightByLabel(label);
    }

    @Override
    public LFXLight getLightByID(String id) {
        return lights.getLightByID(id);
    }   
    

    @Override
    public void addLightCollectionListener(LFXLightCollectionListener listener) {
        lights.addLightCollectionListener(listener);
    }

    @Override
    public void removeLightCollectionListener(LFXLightCollectionListener listener) {
        lights.removeLightCollectionListener(listener);
    } 
    
    public void open() {
    }

    public void close() {
        clear();
        
        // TODO: i am not a fan of this        
        allLightsLoaded = new CountDownLatch(1);        
    }
    
    
    public boolean waitForInitLoaded(long timeout, TimeUnit unit) throws InterruptedException {
        allLightsLoaded.await(timeout, unit);
        return allLightsLoaded.getCount() == 0;
    }
    
    public void handleMessage(LFXMessageRouter router, LFXTimerQueue timer, Set<LFXDeviceID> targets, LFXMessage message) {
        for(LFXDeviceID device: targets) {
            LFXLightImpl light = lights.get(device);
            if(light == null) {
                light = new LFXLightImpl(router, timer, device);
                router.sendMessage(new LFXMessage(LxProtocol.Type.LX_PROTOCOL_DEVICE_GET_LABEL, light.getTarget()));
                router.sendMessage(new LFXMessage(LxProtocol.Type.LX_PROTOCOL_DEVICE_GET_POWER, light.getTarget()));
                router.sendMessage(new LFXMessage(LxProtocol.Type.LX_PROTOCOL_DEVICE_GET_TIME, light.getTarget()));                                
                light.getDetails().load();
            }
            light.handleMessage(message);
            
            if(!lights.contains(light)) {
                lights.add(light);
            }
        }
        
        if(allLightsLoaded.getCount() > 0) {
            for(LFXLight l: lights) {
                if(((LFXLightImpl)l).isLoaded() == false) {
                    return;
                }
            }
            allLightsLoaded.countDown();
        }
    }
    
    /**
     * Go through all lighs and remove the ones we haven't seen for a while.
     */
    public void removeLostLights() {
        Iterator<LFXDeviceID> it = lights.keyIterator();        
        while(it.hasNext()) {
            LFXDeviceID id = it.next();
            LFXLightImpl light = lights.get(id);            
            if(light.isLost()) {
                lights.remove(light);
            } 
        }
    }

    public void clear() {
        lights.clear();
    }    
    
    public LFXLightImpl getLight(LFXDeviceID deviceID) {
        return lights.get(deviceID);        
    }

}
