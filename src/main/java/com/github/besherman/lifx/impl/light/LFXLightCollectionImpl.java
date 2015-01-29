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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is final because it should not be extended. Use composition instead.
 */
public final class LFXLightCollectionImpl implements LFXLightCollection {
    private final Map<LFXDeviceID, LFXLightImpl> lights = new ConcurrentHashMap<>();
    private final List<LFXLightCollectionListener> listeners = new CopyOnWriteArrayList<>();
    
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
        return lights.containsKey(((LFXLightImpl)light).getDeviceID());
    }

    @Override
    public Iterator<LFXLight> iterator() {
        Collection<LFXLight> coll = new ArrayList<>();
        coll.addAll(lights.values());
        return coll.iterator();
    }

    @Override
    public void addLightCollectionListener(LFXLightCollectionListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeLightCollectionListener(LFXLightCollectionListener listener) {
        listeners.remove(listener);
    }    
    
    public LFXLightImpl get(LFXDeviceID device) {
        return lights.get(device);
    }
    
    public Iterator<LFXDeviceID> keyIterator() {
        return Collections.unmodifiableSet(lights.keySet()).iterator();
    }
    
    public void clear() {
        Collection<LFXLight> copy = new ArrayList<>();
        copy.addAll(lights.values());
        lights.clear();
        for(LFXLight light: copy) {
            fireLightRemoved(light);
        }      
    }
    
    public void remove(LFXLightImpl light) {
        if(lights.containsKey(light.getDeviceID())) {
            light.close();
            lights.remove(light.getDeviceID());
            fireLightRemoved(light);
        }        
    }

    public void add(LFXLightImpl light) {
        if(!lights.containsKey(light.getDeviceID())) {
            lights.put(light.getDeviceID(), light);
            fireLightAdded(light);
        }
    }

    @Override
    public LFXLight getLightByLabel(String label) {
        if(label == null) {
            throw new IllegalArgumentException("label can not be null");
        }
        
        for(LFXLight light: this) {
            if(light.getLabel().equals(label)) {
                return light;
            }
        }
        return null;
    }

    @Override
    public LFXLight getLightByID(String id) {
        if(id == null) {
            throw new IllegalArgumentException("id can not be null");
        }
        
        for(LFXLight light: this) {
            if(light.getID().equals(id)) {
                return light;
            }
        }
        return null;        
    }
    
    
    
    private void fireLightAdded(LFXLight light) {        
        for(LFXLightCollectionListener listener: listeners) {
            try {
                listener.lightAdded(light);
            } catch(Exception ex) {
                Logger.getLogger(LFXLightCollectionImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void fireLightRemoved(LFXLight light) {
        for(LFXLightCollectionListener listener: listeners) {
            try {
                listener.lightRemoved(light);
            } catch(Exception ex) {
                Logger.getLogger(LFXLightCollectionImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }  
}
