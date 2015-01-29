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

import com.github.besherman.lifx.impl.entities.internal.LFXTagID;
import com.github.besherman.lifx.LFXLight;
import com.github.besherman.lifx.LFXLightCollectionListener;
import com.github.besherman.lifx.LFXGroup;
import com.github.besherman.lifx.LFXGroupCollection;
import com.github.besherman.lifx.LFXGroupCollectionListener;
import com.github.besherman.lifx.impl.entities.internal.LFXDeviceID;
import com.github.besherman.lifx.impl.entities.internal.LFXMessage;
import com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol;
import com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_DEVICE_STATE_TAGS;
import static com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol.Type.LX_PROTOCOL_DEVICE_STATE_TAG_LABELS;
import com.github.besherman.lifx.impl.entities.internal.structle.LxProtocolDevice;
import com.github.besherman.lifx.impl.network.LFXMessageRouter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Each light has a bitfield (64 bits) which indicates what tags the light has.
 * Each light also has a string array (64 elements) which contains the label
 * of the corresponding tag. So it is technically possible for two lights to have
 * the same tag with different labels. The official apps solve this by always
 * sending tag labels to all lights to keep them in sync. We do the same.
 * 
 * The official apps also thinks that en empty label means that the tag does
 * not exist. So a light can have a tag but if the label is empty it still does
 * not show up.
 * 
 */
public class LFXAllGroups implements LFXGroupCollection {
    private final Map<LFXTagID, LFXGroupImpl> allGroups = new ConcurrentHashMap<>();
    private final Set<LFXGroupImpl> availableGroups = new CopyOnWriteArraySet<>(); 
    private final Object availableLock = new Object();
    private final List<LFXGroupCollectionListener> listeners = new CopyOnWriteArrayList<>();
    private final Set<LFXTagID> hasNotReceivedLabel = Collections.synchronizedSet(EnumSet.allOf(LFXTagID.class));
    private volatile CountDownLatch allLabelsLoaded = new CountDownLatch(1);
    private LFXAllLights allLights;
    private LFXMessageRouter router;
   

    public LFXAllGroups() {
    }
    
    public void setRouter(LFXMessageRouter router) {  
        this.router = router;        
    }
    
    public void setLights(LFXAllLights lights) {
        this.allLights = lights;
        //
        // Since the tags holds references to lights we have to keep them
        // in sync with the lights collection.
        // 
        // TODO: leaking listener
        this.allLights.addLightCollectionListener(new LFXLightCollectionListener() {
            @Override public void lightAdded(LFXLight light) {}
            @Override public void lightRemoved(LFXLight light) {
                for(LFXGroupImpl group: allGroups.values()) {
                    group.removeImpl((LFXLightImpl)light);
                }
            }
        });
    }
    
    
    @Override
    public LFXGroup get(String label) {
        for(LFXGroupImpl group: availableGroups) {
            if(group.getLabel().equals(label)) {
                return group;
            }
        }
        return null;
    }

    @Override
    public LFXGroup add(String label) {
        if(label == null) {
            throw new IllegalArgumentException("label can not be null");
        }
        
        if(label.isEmpty()) {
            throw new IllegalArgumentException("label can not be empty");
        }
        
        LFXGroupImpl firstFreeGroup = null;        
        synchronized(availableLock) {
            // try to find one with the same title first
            for(LFXTagID tagID: LFXTagID.values()) {
                LFXGroupImpl group = allGroups.get(tagID);
                if(group.getLabel().equals(label)) {
                    return group;
                }
            }

            for(LFXTagID tagID: LFXTagID.values()) {
                LFXGroupImpl group = allGroups.get(tagID);
                if(!availableGroups.contains(group)) {
                    firstFreeGroup = group;
                    break;
                }
            }

            if(firstFreeGroup == null) {
                return null;
            }

            firstFreeGroup.setLabel(label);
            updateAvailability(firstFreeGroup);
        }
        return firstFreeGroup;        
    }

    @Override
    public void remove(LFXGroup group) {
        synchronized(availableLock) {
            LFXGroupImpl impl = (LFXGroupImpl)group;
            impl.setLabelImpl("");

            Iterator<LFXLight> it = group.iterator();
            while(it.hasNext()) {
                LFXLightImpl light = (LFXLightImpl)it.next();
                Set<LFXTagID> tags = getTagIDsForLight(light);
                tags.remove(impl.getTagID());            

                LxProtocolDevice.SetTags payload = new LxProtocolDevice.SetTags(LFXTagID.pack(tags));
                LFXMessage msg = new LFXMessage(LxProtocol.Type.LX_PROTOCOL_DEVICE_SET_TAGS, light.getTarget(), payload);
                for(int i = 0; i < 3; i++) {
                    router.sendMessage(msg);                            
                }
            }
            
            impl.clearImpl();
            updateAvailability(impl);
        }        
    }
    
    
    @Override
    public int size() {
        return availableGroups.size();
    }

    @Override
    public boolean isEmpty() {
        return availableGroups.isEmpty();
    }

    @Override
    public Iterator<LFXGroup> iterator() {
        Collection<LFXGroup> copy = new ArrayList<>();
        copy.addAll(availableGroups);
        return copy.iterator();
    }

    @Override
    public void addGroupCollectionListener(LFXGroupCollectionListener l) {
        listeners.add(l);
    }

    @Override
    public void removeGroupCollectionListener(LFXGroupCollectionListener l) {
        listeners.remove(l);
    }    

    @Override
    public boolean contains(LFXGroup group) {
        return get(group.getLabel()) != null;
    }

    @Override
    public boolean contains(LFXLight light) {
        for (LFXGroup group : availableGroups) {
            if (group.contains(light)) {
                return true;
            }
        }

        return false;
    }
    
    public void open() {
        for(LFXTagID id: LFXTagID.values()) {
            allGroups.put(id, new LFXGroupImpl(router, this, id));
        }                
    }

    public void close() {
        for(LFXTagID id: LFXTagID.values()) {
            LFXGroupImpl group = allGroups.remove(id);
            if(group != null && availableGroups.contains(group)) {
                availableGroups.remove(group);
                fireGroupRemoved(group);
            }
        }     
        
        // TODO: i am not a fan of this
        hasNotReceivedLabel.addAll(EnumSet.allOf(LFXTagID.class));
        allLabelsLoaded = new CountDownLatch(1);
    }
    
    
    public boolean isLoaded() {
        return hasNotReceivedLabel.isEmpty();
    }
    
    public boolean waitForInitLoaded(long timeout, TimeUnit unit) throws InterruptedException {
        allLabelsLoaded.await(timeout, unit);
        return isLoaded();        
    }
    
    
    public void sendAddLightToGroup(LFXLightImpl light, LFXGroupImpl group) {
        Set<LFXTagID> tags = getTagIDsForLight(light);
        tags.add(group.getTagID());
        LxProtocolDevice.SetTags payload = new LxProtocolDevice.SetTags(LFXTagID.pack(tags));            
        for(int i = 0; i < 3; i++) {
            router.sendMessage(new LFXMessage(Type.LX_PROTOCOL_DEVICE_SET_TAGS, light.getTarget(), payload));        
        }
    }
    
    public void sendRemoveLightToGroup(LFXLightImpl light, LFXGroupImpl group) {
        Set<LFXTagID> tags = getTagIDsForLight(light);
        tags.remove(group.getTagID());
        LxProtocolDevice.SetTags payload = new LxProtocolDevice.SetTags(LFXTagID.pack(tags));            
        for(int i = 0; i < 3; i++) {
            router.sendMessage(new LFXMessage(Type.LX_PROTOCOL_DEVICE_SET_TAGS, light.getTarget(), payload));        
        }
    }
    
    /**
     * Returns all tags that the light has.
     */
    public Set<LFXTagID> getTagIDsForLight(LFXLightImpl light) {
        Set<LFXTagID> result = new HashSet<>();
        for(LFXGroupImpl group: availableGroups) {
            if(group.contains(light)) {
                result.add(group.getTagID());
            }
        }
        return result;
    }    
    
    
    public void handleMessage(Set<LFXDeviceID> targets, LFXMessage message) {
        Type type = message.getType();
        if(type == LX_PROTOCOL_DEVICE_STATE_TAGS) {
            LxProtocolDevice.StateTags payload = message.getPayload();
            Set<LFXTagID> ids = LFXTagID.unpack(payload.getTags());                            
            setLightGroups(targets, ids);
        } else if(type == LX_PROTOCOL_DEVICE_STATE_TAG_LABELS) {
            LxProtocolDevice.StateTagLabels payload = message.getPayload();
            Set<LFXTagID> tags = LFXTagID.unpack(payload.getTags());
            String label = payload.getLabel();            
            if(targets.size() == 1) {
                setGroupLabels(targets.iterator().next(), tags, label); 
            }
        }        
    }

    private void setLightGroups(Set<LFXDeviceID> targets, Set<LFXTagID> ids) {
        for(LFXDeviceID deviceId: targets) {
            LFXLightImpl light = allLights.getLight(deviceId);
            for(LFXGroupImpl group: allGroups.values()) {
                if(ids.contains(group.getTagID())) {
                    group.addImpl(light);
                } else {
                    group.removeImpl(light);
                }

                updateAvailability(group);
            }
        }                
    }

    private void setGroupLabels(LFXDeviceID source, Set<LFXTagID> ids, String label) {        
        for(LFXTagID id: ids) {
            LFXGroupImpl group = allGroups.get(id);
            if(group.contains(source)) {
                group.labelDidChangeTo(label);
                updateAvailability(group);
            } 
        }
        
        if(!hasNotReceivedLabel.isEmpty()) {
            hasNotReceivedLabel.removeAll(ids);
            if(hasNotReceivedLabel.isEmpty()) {
                allLabelsLoaded.countDown();
            }
        }
    }
    

    
    
    private void updateAvailability(LFXGroupImpl group) {
        if(group.isAvaliable() && !availableGroups.contains(group)) {
            availableGroups.add(group);
            fireGroupAdded(group);
        } else if(!group.isAvaliable() && availableGroups.contains(group)) {
            availableGroups.remove(group);
            fireGroupRemoved(group);                    
        }
    }
    
    private void fireGroupAdded(LFXGroupImpl group) {
        for(LFXGroupCollectionListener l: listeners) {
            try {
                l.groupAdded(group);
            } catch(Exception ex) {
                Logger.getLogger(LFXAllGroups.class.getName()).log(Level.SEVERE, 
                        "GroupCollectionListener failed", ex);
            }
        }
    }

    private void fireGroupRemoved(LFXGroupImpl group) {
        for(LFXGroupCollectionListener l: listeners) {
            try {
                l.groupRemoved(group);
            } catch(Exception ex) {
                Logger.getLogger(LFXAllGroups.class.getName()).log(Level.SEVERE, 
                        "GroupCollectionListener failed", ex);
            }
        }        
    }    

}
