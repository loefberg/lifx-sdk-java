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
package com.github.besherman.lifx.impl.network;

import com.github.besherman.lifx.impl.entities.internal.LFXTagID;
import com.github.besherman.lifx.impl.entities.internal.LFXBinaryPath;
import com.github.besherman.lifx.impl.entities.internal.LFXBinaryTargetType;
import com.github.besherman.lifx.impl.entities.internal.LFXDeviceID;
import com.github.besherman.lifx.impl.entities.internal.LFXMessage;
import com.github.besherman.lifx.impl.entities.internal.LFXSiteID;
import com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol;
import com.github.besherman.lifx.impl.entities.internal.structle.LxProtocolDevice;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Richard
 */
public class LFXRoutingTable {
    private final Map<LFXSiteID, GatewayEntry> gateways = new HashMap<>();
    private final Map<LFXDeviceID, LightEntry> lights = new HashMap<>();

    public LFXRoutingTable() {
    }
    
    // 
    // Information needed handle message
    //
        
    /**
     * Returns all lights at the given site.
     */
    public synchronized Set<LFXDeviceID> getLightsAtSite(LFXSiteID siteID) {
        Set<LFXDeviceID> result = new HashSet<>();
        for(LightEntry light: lights.values()) {
            if(light.isSite(siteID)) {
                result.add(light.getDevice());
            }
        }
        return result;
    }

    /**
     * Return all lights at the site that has one or more of the given tags.
     */
    public synchronized Set<LFXDeviceID> getLightsAtSiteWithTags(LFXSiteID siteID, Set<LFXTagID> groupTagField) {
        Set<LFXDeviceID> result = new HashSet<>();
        for(LightEntry light: lights.values()) {
            if(light.isSite(siteID) && light.hasTags(groupTagField)) {
                result.add(light.getDevice());
            }
        }
        return result;
    }
    
    
    
    
    // 
    // Information needed to create the binary path, before sending
    // 
    
    /**
     * Returns all sites we know of.
     */
    public synchronized Set<LFXSiteID> getAllSites() {
        return new HashSet<>(gateways.keySet());
    }

    /**
     * Returns the light's site id.
     */
    public synchronized LFXSiteID getLightsSiteID(LFXDeviceID deviceID) {
        LightEntry entry = lights.get(deviceID);
        if(entry != null) {
            return entry.getSite();
        }
        return null;
    }

    /**
     * Returns all sites that has a light with the given tag.
     */
    public synchronized Set<LFXSiteID> getSiteIDsWhereLightHasTag(LFXTagID tag) {
        Set<LFXSiteID> sites = new HashSet<>();
        for(LightEntry entry: lights.values()) {
            if(entry.hasTag(tag)) {
                sites.add(entry.getSite());
            }
        }
        return sites;
    }
    
    
    
    //
    // Information needed when sending a message
    //
    
    /**
     * Returns the addresses to all gateways we know of.
     */
    public synchronized Collection<InetSocketAddress> getAllSiteAddresses() {
        Set<InetSocketAddress> addresses = new HashSet<>();
        for(GatewayEntry gw: gateways.values()) {
            addresses.add(gw.getAddress());
        }            
        return addresses;        
    }
    
    /**
     * Returns the address of the given site.
     */
    public synchronized InetSocketAddress getAddressForSiteID(LFXSiteID siteID) {
        GatewayEntry entry = gateways.get(siteID);        
        return entry != null ? entry.getAddress() : null;
    } 
    
    
    //
    //
    //
    
    public synchronized boolean isLightStillAlive(LFXDeviceID deviceID) {
        return lights.containsKey(deviceID);
    }

    
    /**
     * Updates the routing table with information about PAN gateways.
     * 
     * This is its distinct from updateTable() because the caller wants to know
     * if a new gateway was found so that it can ask it for new lights.
     * 
     * @return the site id if a new gateway was found
     */
    public synchronized LFXSiteID updateTableWithPAN(LFXMessage message) {
        LFXSiteID newGatewayDiscovered = null;
        LFXBinaryPath path = message.getPath();
        
        if(message.getType() == LxProtocol.Type.LX_PROTOCOL_DEVICE_STATE_PAN_GATEWAY) {
            LxProtocolDevice.StatePanGateway statePanGatewayPayload = (LxProtocolDevice.StatePanGateway) message.getPayload();

            InetAddress host = message.getSourceNetworkHost();
            int port = (int) statePanGatewayPayload.getPort().getValue();
            LFXSiteID site = path.getSiteID();
            LxProtocolDevice.Service service = LxProtocolDevice.serviceMap.get(statePanGatewayPayload.getService().getValue());

            if (service == LxProtocolDevice.Service.LX_PROTOCOL_DEVICE_SERVICE_TCP) {
                // TODO: can this happen?
                return null;
            }

            GatewayEntry gateway = gateways.get(site);
            if(gateway == null) {   
                Logger.getLogger(LFXRoutingTable.class.getName()).log(Level.FINE, "Found new gateway: {0}", site);
                gateway = new GatewayEntry(new InetSocketAddress(host, port), site);
                gateways.put(site, gateway);
                newGatewayDiscovered = site;
            } else {
                gateway.refresh(new InetSocketAddress(host, port));
            }        
        }

        removeStaleGateways();
     
        return newGatewayDiscovered;
    }
    
    /**
     * Updates the routing table from information in the message.
     */
    public synchronized LFXDeviceID updateTableWithLight(LFXMessage message) {
        LFXDeviceID newLightDiscovered = null;
        LFXBinaryPath path = message.getPath();
        
        if(path.getBinaryTargetID().getTargetType() == LFXBinaryTargetType.DEVICE) {
            LFXSiteID site = path.getSiteID();
            LFXDeviceID device = path.getBinaryTargetID().getDeviceID();
            
            LightEntry entry = lights.get(device);
            if(entry == null) {
                Logger.getLogger(LFXRoutingTable.class.getName()).log(Level.FINE, "Found new light: {0}", device);
                entry = new LightEntry(device);
                lights.put(device, entry);
                newLightDiscovered = device;
            }            
            entry.refresh(site);
        }
        
        if(message.getType() == LxProtocol.Type.LX_PROTOCOL_DEVICE_STATE_TAGS) {
            LxProtocolDevice.StateTags payload = message.getPayload();            
            LFXDeviceID device = path.getBinaryTargetID().getDeviceID();
            Set<LFXTagID> tags = LFXTagID.unpack(payload.getTags());
                        
            LightEntry entry = lights.get(device);
            if(entry != null) {
                entry.setTags(tags);
            } else {
                // If we get tags information before we have discovered the 
                // light we ignore it... right?                
            }
        }
        
        removeStaleGateways();
        removeStaleLights();      
        
        return newLightDiscovered;
    }
    
    /**
     * Removed gateways that we haven't seen for a while.
     */
    private void removeStaleGateways() {
        Iterator<LFXSiteID> it = gateways.keySet().iterator();
        while(it.hasNext()) {
            LFXSiteID site = it.next();
            GatewayEntry gateway = gateways.get(site);
            if(gateway.isLost()) {
                it.remove();
            }
        }
    }
    
    /**
     * Removes lights we haven't heard from in a while. Returns the lost lights.
     */
    private void removeStaleLights() {
        Iterator<LFXDeviceID> it = lights.keySet().iterator();
        while(it.hasNext()) {
            LFXDeviceID id = it.next();
            LightEntry entry = lights.get(id);
            if(entry.isLost()) {
                it.remove();
            }
        }        
    }

    
    private static class LightEntry {
        private final LFXDeviceID device;
        private LFXSiteID site;
        private long lastSeen = System.currentTimeMillis();
        private Set<LFXTagID> tags = new HashSet<>();

        public LightEntry(LFXDeviceID device) {
            this.device = device;
            this.site = new LFXSiteID();
        }    
        
        public LFXDeviceID getDevice() {
            return device;
        }
        
        public boolean isSite(LFXSiteID site) {
            return this.site.equals(site);
        }
        
        
        public void refresh(LFXSiteID site) {
            this.site = site;
            this.lastSeen = System.currentTimeMillis();
        }
        
        public boolean isLost() {
            // TODO: configure this
            return (System.currentTimeMillis() - lastSeen) > 35 * 1000;
        }        

        private boolean hasTags(Set<LFXTagID> groupTagField) {
            for(LFXTagID tag: groupTagField) {
                if(tags.contains(tag)) {
                    return true;
                }
            }
            return false;
        }

        private LFXSiteID getSite() {
            return site;
        }

        private boolean hasTag(LFXTagID tag) {
            return tags.contains(tag);
        }     

        private void setTags(Set<LFXTagID> tags) {
            this.tags = tags;
        }

        @Override
        public String toString() {
            return "LightEntry{" + "device=" + device + ", site=" + site + ", lastSeen=" + lastSeen + ", tags=" + tags + '}';
        }
        
        
    } 
    
    private static class GatewayEntry {        
        private final LFXSiteID site;
        
        private InetSocketAddress address;
        private long lastSeen = System.currentTimeMillis();

        public GatewayEntry(InetSocketAddress address, LFXSiteID site) {
            this.address = address;
            this.site = site;
        }
        
        public void refresh(InetSocketAddress address) {
            this.address = address;
            lastSeen = System.currentTimeMillis();
        }
        
        public boolean isLost() {
            // TODO: configure this
            return (System.currentTimeMillis() - lastSeen) > 20 * 1000;
        }

        public InetSocketAddress getAddress() {
            return address;
        }

        public LFXSiteID getSiteID() {
            return site;
        }

        @Override
        public String toString() {
            return "GatewayEntry{" + "site=" + site + ", address=" + address + ", lastSeen=" + lastSeen + '}';
        }
        
        

    }
    
}
