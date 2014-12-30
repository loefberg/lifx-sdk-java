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
import com.github.besherman.lifx.impl.network.LFXMessageRouter;
import com.github.besherman.lifx.impl.network.LFXTimerQueue;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Richard
 */
public class LFXAllLights implements LFXLightCollection {        
    private final Map<LFXDeviceID, LFXLightImpl> notLoadedLights = new ConcurrentHashMap<>();
    private final CountDownLatch allLightsLoaded = new CountDownLatch(1);
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
    public void addLightCollectionListener(LFXLightCollectionListener listener) {
        lights.addLightCollectionListener(listener);
    }

    @Override
    public void removeLightCollectionListener(LFXLightCollectionListener listener) {
        lights.removeLightCollectionListener(listener);
    }    
    
    public boolean waitForInitLoaded(long timeout, TimeUnit unit) throws InterruptedException {
        allLightsLoaded.await(timeout, unit);
        return allLightsLoaded.getCount() == 0;
    }
    
    public void handleMessage(LFXMessageRouter router, LFXTimerQueue timer, Set<LFXDeviceID> targets, LFXMessage message) {
        for(LFXDeviceID device: targets) {
            LFXLightImpl light = lights.get(device);
            boolean notLoaded = false;
            if(light == null) {
                notLoaded = true;
                light = notLoadedLights.get(device);
                if(light == null) {
                    light = new LFXLightImpl(router, timer, device);
                    light.getDetails().load();
                    light.getAlarms().load();
                    notLoadedLights.put(device, light);
                }
            }
            light.handleMessage(message);
            
            if(notLoaded && light.isLoaded()) {
                lights.add(light);
                notLoadedLights.remove(light.getDeviceID());
                if(notLoadedLights.isEmpty()) {
                    allLightsLoaded.countDown();
                }
            }
        }    
    }
    
    public void printReasons() {
        for(LFXLightImpl light: notLoadedLights.values()) {
            System.out.println(light.getID() + " " + light.getMessagesUntilLoaded());
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
        
        it = notLoadedLights.keySet().iterator();
        while(it.hasNext()) {
            LFXDeviceID id = it.next();
            LFXLightImpl light = notLoadedLights.get(id);            
            if(light.isLost()) {
                light.close();
                it.remove();
            } 
        }
        
    }

    public void clear() {
        lights.clear();
        notLoadedLights.clear();
    }
    
    
    public LFXLightImpl getLightLodedOrNot(LFXDeviceID deviceID) {
        LFXLightImpl light = lights.get(deviceID);        
        return light != null ? light : notLoadedLights.get(deviceID);
    }
}
