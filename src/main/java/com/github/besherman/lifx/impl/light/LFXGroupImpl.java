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

import com.github.besherman.lifx.LFXFuzzyPower;
import com.github.besherman.lifx.impl.entities.internal.LFXTagID;
import com.github.besherman.lifx.LFXGroup;
import com.github.besherman.lifx.LFXHSBKColor;
import com.github.besherman.lifx.LFXLight;
import com.github.besherman.lifx.LFXLightCollectionListener;
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
import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Richard
 */
public class LFXGroupImpl implements LFXGroup {
    private final LFXTagID id;
    private final LFXMessageRouter router;
    private final LFXAllGroups groups;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private final LFXLightCollectionImpl lights = new LFXLightCollectionImpl();
    
    private String label = "";
    

    public LFXGroupImpl(LFXMessageRouter router, LFXAllGroups groups, LFXTagID id) {        
        this.id = id;
        this.router = router;
        this.groups = groups;
    }

    @Override
    public String getID() {
        return getTagID().toString();
    }
    
    public LFXTagID getTagID() {
        return id;
    }
    
    public boolean isAvaliable() {
        return !getLabel().isEmpty();
    }
    
    public LFXTarget getTarget() {
        return new LFXTarget(id);
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "GroupImpl{" + "id=" + id + ", label=" + label + '}';
    }
    
    @Override
    public boolean isLabelAllowed(String newLabel) {
        if(newLabel == null || newLabel.isEmpty()) {
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
        setLabelImpl(label);
    }

    @Override
    public LFXFuzzyPower getPower() {        
        Iterator<LFXLight> it = iterator();
        if(it.hasNext()) {
            boolean power = it.next().isPower();
            while(it.hasNext()) {
                if(it.next().isPower() != power) {
                    return LFXFuzzyPower.MIXED;
                }
            }
            return power ? LFXFuzzyPower.ON : LFXFuzzyPower.OFF;
        } else {
            return LFXFuzzyPower.OFF;
        }
    }
    
    @Override
    public void setPower(boolean power) {
        LFXPowerState state = power ? LFXPowerState.ON : LFXPowerState.OFF;
        StructleTypes.UInt16 protocolPowerLevel = LFXBinaryTypes.getLFXProtocolPowerLevelFromLFXPowerState(state);
        LxProtocolDevice.SetPower payload = new LxProtocolDevice.SetPower(protocolPowerLevel);
        LFXMessage message = new LFXMessage(LxProtocol.Type.LX_PROTOCOL_DEVICE_SET_POWER, getTarget(), payload);
        for(int i = 0; i < 3; i++) {
            router.sendMessage(message);
        }
        
        for(LFXLight light: lights) {
            ((LFXLightImpl)light).powerDidChangeTo(state);
        }
    }

    @Override
    public LFXHSBKColor getAverageColor() {
        List<LFXHSBKColor> colors = new ArrayList<>();
        for(LFXLight light: lights) {
            colors.add(light.getColor());
        }
        
        return LFXHSBKColor.averageOfColors(colors.toArray(new LFXHSBKColor[0]));
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
        
        // TODO: fire event
        
        StructleTypes.UInt8 stream = new StructleTypes.UInt8(0);
        LxProtocolLight.Hsbk protocolColor = LFXBinaryTypes.getLXProtocolLightHsbkFromLFXHSBKColor(color);
        StructleTypes.UInt32 protocolDuration = new StructleTypes.UInt32(duration);
        LxProtocolLight.Set payload = new LxProtocolLight.Set(stream, protocolColor, protocolDuration);
        LFXMessage message = new LFXMessage(LxProtocol.Type.LX_PROTOCOL_LIGHT_SET, getTarget(), payload);        
        
        router.sendMessage(message);
        
        for(LFXLight light: lights) {
            ((LFXLightImpl)light).colorDidChangeTo(color);
        }        
    }

    @Override
    public void add(LFXLight light) {
        LFXLightImpl impl = (LFXLightImpl)light;
        if(!contains(impl)) {
            lights.add(impl);
            groups.sendAddLightToGroup(impl, this);
        }
    }

    @Override
    public void remove(LFXLight light) {
        LFXLightImpl impl = (LFXLightImpl)light;
        if(contains(impl)) {
            lights.remove(impl);
            groups.sendRemoveLightToGroup(impl, this);
        }        
    }
    
    @Override
    public LFXLight getLightByLabel(String label) {
        return lights.getLightByLabel(label);
    }

    @Override
    public LFXLight getLightByID(String id) {
        return lights.getLightByID(id);
    }   
    
    
    /**
     * Removes light without sending message to network.
     */
    public void removeImpl(LFXLightImpl light) {
        lights.remove(light);
    }

    /**
     * Adds light without sending message to network.
     * @param light 
     */
    public void addImpl(LFXLightImpl light) {
        lights.add(light);
    }
    
    /**
     * Removes all lights without sending message to network.
     */
    public void clearImpl() {
        lights.clear();
    }
    
    
    /**
     * Sets the label without triggering a message to the light.
     */
    public void labelDidChangeTo(String label) {
        String old = this.label;
        this.label = label;
        pcs.firePropertyChange("label", old, label);        
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

    /**
     * Sets the label, without checking the length. And sends a message to
     * the light.
     */
    public void setLabelImpl(String label) {
        Set<LFXTagID> tag = EnumSet.of(id);
        LxProtocolDevice.SetTagLabels payload = new LxProtocolDevice.SetTagLabels(LFXTagID.pack(tag), label);
        // note that we send this to all lights
        LFXMessage msg = new LFXMessage(LxProtocol.Type.LX_PROTOCOL_DEVICE_SET_TAG_LABELS, LFXTarget.getBroadcastTarget(), payload);
        for(int i = 0; i < 3; i++) {
            router.sendMessage(msg);                
        }
        
        labelDidChangeTo(label);        
    }

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
    
    public boolean contains(LFXDeviceID device) {
        return lights.get(device) != null;
    }    

    @Override
    public Iterator<LFXLight> iterator() {
        return lights.iterator();
    }

    @Override
    public void addLightCollectionListener(LFXLightCollectionListener l) {
        lights.addLightCollectionListener(l);
    }

    @Override
    public void removeLightCollectionListener(LFXLightCollectionListener l) {
        lights.removeLightCollectionListener(l);
    }
    
}
