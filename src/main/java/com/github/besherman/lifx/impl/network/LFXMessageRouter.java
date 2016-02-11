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

import com.github.besherman.lifx.impl.entities.internal.LFXBinaryPath;
import com.github.besherman.lifx.impl.entities.internal.LFXMessage;
import com.github.besherman.lifx.impl.entities.internal.LFXBinaryTargetID;
import com.github.besherman.lifx.impl.entities.internal.LFXDeviceID;
import com.github.besherman.lifx.impl.entities.internal.LFXSiteID;
import com.github.besherman.lifx.impl.entities.internal.LFXTarget;
import com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The message router passes messages between {@link LFXNetworkLoop} and 
 * {@link LFXLightHandler}. In order to know which light should get what message
 * and where to send messages it also tracks routing information.
 */
public class LFXMessageRouter {    
    private final LFXRoutingTable routingTable = new LFXRoutingTable();    
    private final LFXNetworkSettings networkSettings;
    private final LFXLightHandlerModel handlers;
    private final BlockingQueue<LFXSocketMessage> outgoingQueue;
    
    private final AtomicBoolean opened = new AtomicBoolean(false);    
    private final CountDownLatch firstPANReceived = new CountDownLatch(1);    
    private final LFXResponseTracker responseTracker = new LFXResponseTracker(routingTable);
    
    
    private LFXTimerQueue timerQueue;
    

    public LFXMessageRouter(String broadcastAddress, LFXLightHandlerModel handlers, BlockingQueue<LFXSocketMessage> outgoingQueue) {
        networkSettings = new LFXNetworkSettings(broadcastAddress);
        this.handlers = handlers;
        this.outgoingQueue = outgoingQueue;
        this.responseTracker.setOutgoingQueue(outgoingQueue);
    }
    
    /**
     * Waits for the first PAN message to arrive. This is the message that
     * the "responsible" light sends out and is going to be the first message
     * we receive - if there are any lights on the network.
     * 
     * If there are no lights on the network this message will never come 
     * and we can give up looking early.
     */
    public boolean waitForInitPAN(long timeout, TimeUnit unit) throws InterruptedException {
        firstPANReceived.await(timeout, unit);
        return firstPANReceived.getCount() == 0;
    }
    
    public void open() {
        if(!opened.getAndSet(true)) {
            timerQueue = new LFXTimerQueue();  

            responseTracker.open();

            for(int i = 0; i < 5; i++) {
                timerQueue.doLater(sendGatewayDiscoveryAction, i, TimeUnit.SECONDS);                    
            }                

            timerQueue.doRepeatedly(sendGatewayDiscoveryAction, 15, TimeUnit.SECONDS);

            handlers.forEach(new LFXLightHandlerModelConsumer() {
                @Override
                public void accept(LFXLightHandler handler) {
                    try {
                        handler.setRouter(LFXMessageRouter.this);
                        handler.open();
                    } catch(Exception ex) {
                        Logger.getLogger(LFXMessageRouter.class.getName()).log(Level.SEVERE, 
                                "Failed to open LightHandler", ex);
                    }                    
                }
            });  
            
            // TODO: what if a handler was added between previous and next lines?
            handlers.addLightHandlerModelListener(lightHandlerModelListener);
            
        } else {
            Logger.getLogger(LFXMessageRouter.class.getName()).log(Level.SEVERE, 
                    "MessageRouter already opened");
        }
    }
    

    
    public void close() {
        if(opened.getAndSet(false)) {
            handlers.removeLightHandlerModelListener(lightHandlerModelListener);
            
            handlers.forEach(new LFXLightHandlerModelConsumer() {
                @Override
                public void accept(LFXLightHandler handler) {
                    try {
                        handler.close();
                        handler.setRouter(null);
                    } catch(Exception ex) {
                        Logger.getLogger(LFXMessageRouter.class.getName()).log(Level.SEVERE, 
                                "Failed to close LightHandler", ex);
                    }
                }
            });            
            
            
            responseTracker.close();

            timerQueue.close();        
        } else {
            Logger.getLogger(LFXMessageRouter.class.getName()).log(Level.SEVERE, 
                    "MessageRouter already closed");
        }
    }    
    
    public void handleMessage(LFXMessage message) { 
        if(!message.isAResponseMessage()) {
            return;
        }        
        
        
        if(message.getType() == LxProtocol.Type.LX_PROTOCOL_DEVICE_STATE_PAN_GATEWAY) {
            LFXSiteID newSite = routingTable.updateTableWithPAN(message);
            if(newSite != null) {            
                firstPANReceived.countDown();

                // We want to find all lights as soon as possible, but we have to 
                // know about the gateways before we can do that. So when we get
                // info about a gateway we look for lights.
                //
                // Later we'll periodically ask for new lights every 15 seconds
                //
                Runnable sendGetLights = new SendGetLightsFromSite(newSite);
                sendGetLights.run();
                timerQueue.doLater(sendGetLights, 100, TimeUnit.MILLISECONDS);                
                timerQueue.doLater(sendGetLights, 200, TimeUnit.MILLISECONDS);
            } 
        } else {    
            responseTracker.updateReponse(message);
            
            LFXDeviceID newDevice = routingTable.updateTableWithLight(message);
            if(newDevice != null) {
                // the routing table needs to know about tags, so we ask about
                // them when we find a new light                
                sendMessage(new LFXMessage(LxProtocol.Type.LX_PROTOCOL_DEVICE_GET_TAGS, new LFXTarget(newDevice)));
            }
            
            
            LFXBinaryPath path = message.getPath();

            Set<LFXDeviceID> targets = new HashSet<>();
            switch (path.getBinaryTargetID().getTargetType()) {
                case BROADCAST: {
                    targets.addAll(routingTable.getLightsAtSite(path.getSiteID()));                
                    break;
                }

                case DEVICE: {                
                    targets.add(path.getBinaryTargetID().getDeviceID());
                    break;
                }

                case TAG: {
                    targets.addAll(routingTable.getLightsAtSiteWithTags(path.getSiteID(), path.getBinaryTargetID().getGroupTagField()));
                    break;
                }
            }
            
            passMessageToHandlers(targets, message);            
        }        
    }
    
    private void passMessageToHandlers(final Set<LFXDeviceID> targets, final LFXMessage message) {
        handlers.forEach(new LFXLightHandlerModelConsumer() {
            @Override
            public void accept(LFXLightHandler handler) {
                try {                    
                    handler.handleMessage(targets, message);
                } catch(Exception ex) {
                    Logger.getLogger(LFXMessageRouter.class.getName()).log(Level.SEVERE, 
                            "Failed to handle message", ex);
                }                
            }
        });                    
    }

    
    ////////////////////////////////////////////////////////////////////////////
    //
    ////////////////////////////////////////////////////////////////////////////
    
    public void sendMessage(LFXMessage message) {
        // For messages that have their Target set        
        LFXTarget target = message.getTarget();
        if (target != null) {
            List<LFXBinaryPath> binaryPaths = new ArrayList<>();
            switch (target.getTargetType()) {
                case BROADCAST: {
                    for(LFXSiteID site: routingTable.getAllSites()) {
                        LFXBinaryPath path = new LFXBinaryPath(site, new LFXBinaryTargetID());
                        binaryPaths.add(path);
                    }
                    break;
                }
                case DEVICE: {
                    LFXSiteID site = routingTable.getLightsSiteID(target.getDeviceID());
                    binaryPaths.add(new LFXBinaryPath(site, new LFXBinaryTargetID(target.getDeviceID())));
                    break;                    
                }
                case TAG: {
                    for(LFXSiteID site: routingTable.getSiteIDsWhereLightHasTag(target.getTag())) {
                        LFXBinaryTargetID targetID = new LFXBinaryTargetID(EnumSet.of(target.getTag()));
                        binaryPaths.add(new LFXBinaryPath(site, targetID));                        
                    }
                    break;
                }
            }
            
            if(binaryPaths.isEmpty()) {
                // well, there can be two cases for this:
                //   1) broadcast message before we know of any gateways
                //   2) sending to a tag that has no lights
                //
                // TODO: do something about this
            }
            
            for(LFXBinaryPath path: binaryPaths) {
                sendWithPath(message.withPath(path));
            }     
        } else if(message.getPath() != null) {
            // For message that have their Binary Path set explicitly (for internal use only)
            sendWithPath(message);
        } else {
            throw new RuntimeException("message has neither target nor path");
        }
    } 
    
    private void sendWithPath(LFXMessage message) {
        if (message.getPath().getSiteID().isZeroSite()) {
            // send to all gateways
            for (InetSocketAddress address : routingTable.getAllSiteAddresses()) {
                sendToAddress(message, address);
            }
        } else {            
            InetSocketAddress address = routingTable.getAddressForSiteID(message.getPath().getSiteID());
            if(address != null) {
                sendToAddress(message, address);                
            } else {
                // this should not happen
                Logger.getLogger(LFXMessageRouter.class.getName()).log(Level.SEVERE, 
                        "No address for gateway, this should not happen");
            }            
        }        
    }
    
    private void sendBroadcast(LFXMessage message) {
        InetSocketAddress broadcastAddress;
        try {
            broadcastAddress = networkSettings.getBroadcast();
        } catch(SocketException ex) {
            Logger.getLogger(LFXMessageRouter.class.getName()).log(Level.SEVERE, 
                    "Failed to get broadcast address", ex);
            return;
        }
        
        sendToAddress(message, broadcastAddress);
    }
    
    int maxQueueLength = 0;
    
    private void sendToAddress(LFXMessage message, InetSocketAddress address) {
        int messagesInQueue = outgoingQueue.size();
        if(messagesInQueue > maxQueueLength) {
            //Logger.getLogger(LFXMessageRouter.class.getName()).log(Level.INFO, "New max queue size is " + messagesInQueue);
            maxQueueLength = messagesInQueue;
        }
        
        byte[] messageData = message.getMessageDataRepresentation();
        LFXSocketMessage sm = new LFXSocketMessage(messageData, address, getMessagePriority(message));
        if(!outgoingQueue.offer(sm)) {
            Logger.getLogger(LFXMessageRouter.class.getName()).log(Level.SEVERE, 
                    "Failed to send message, queue is full");
        }         
        responseTracker.trackResponse(message, sm);
    }

    /**
     * Give higher priority to SET messages because this is what the user sees
     * so it improves the experience.
     */
    private int getMessagePriority(LFXMessage message) {
        switch(message.getType()) {
            case LX_PROTOCOL_DEVICE_SET_SITE: 
            case LX_PROTOCOL_DEVICE_SET_TIME: 
            case LX_PROTOCOL_DEVICE_SET_DUMMY_LOAD: 
            case LX_PROTOCOL_DEVICE_SET_POWER: 
            case LX_PROTOCOL_DEVICE_SET_LABEL: 
            case LX_PROTOCOL_DEVICE_SET_TAGS: 
            case LX_PROTOCOL_DEVICE_SET_TAG_LABELS: 
            case LX_PROTOCOL_DEVICE_SET_FACTORY_TEST_MODE: 
            case LX_PROTOCOL_LIGHT_SET: 
            case LX_PROTOCOL_LIGHT_SET_WAVEFORM: 
            case LX_PROTOCOL_LIGHT_SET_DIM_ABSOLUTE: 
            case LX_PROTOCOL_LIGHT_SET_DIM_RELATIVE: 
            case LX_PROTOCOL_LIGHT_SET_RGBW: 
            case LX_PROTOCOL_LIGHT_SET_CALIBRATION_COEFFICIENTS: 
            case LX_PROTOCOL_LIGHT_SET_SIMPLE_EVENT: 
            case LX_PROTOCOL_LIGHT_SET_POWER: 
            case LX_PROTOCOL_LIGHT_SET_WAVEFORM_OPTIONAL: 
            case LX_PROTOCOL_WIFI_SET: 
            case LX_PROTOCOL_WIFI_SET_ACCESS_POINT: 
                return LFXSocketMessage.HIGH_PRIORITY;
        }
        
        return LFXSocketMessage.LOW_PRIORITY;
    }
    
    
    ////////////////////////////////////////////////////////////////////////////
    //
    ////////////////////////////////////////////////////////////////////////////
    
    
    private final Runnable sendGatewayDiscoveryAction = new Runnable() {
        @Override
        public void run() {
            for(int i = 0; i < 3; i++) {
                sendBroadcast(new LFXMessage(LxProtocol.Type.LX_PROTOCOL_DEVICE_GET_PAN_GATEWAY));                
            }
        }        
    };    
    
    
    private class SendGetLightsFromSite implements Runnable {
        private final LFXSiteID site;

        public SendGetLightsFromSite(LFXSiteID site) {
            this.site = site;
        }

        @Override
        public void run() {
            sendMessage(new LFXMessage(LxProtocol.Type.LX_PROTOCOL_LIGHT_GET, new LFXBinaryPath(site)));                            
        }
    }
    
    
    private final LFXLightHandlerModelListener lightHandlerModelListener = new LFXLightHandlerModelListener() {

        @Override
        public void handlerAdded(LFXLightHandler handler) {            
            if(opened.get()) {
                try {
                    handler.setRouter(LFXMessageRouter.this);
                    handler.open();
                } catch(Exception ex) {
                    Logger.getLogger(LFXMessageRouter.class.getName()).log(Level.SEVERE, 
                            "Failed to open LightHandler", ex);
                }                
            }
        }

        @Override
        public void handlerRemoved(LFXLightHandler handler) {
            try {                
                handler.close();
                handler.setRouter(null);
            } catch(Exception ex) {
                Logger.getLogger(LFXMessageRouter.class.getName()).log(Level.SEVERE, 
                        "Failed to close LightHandler", ex);
            }
        }
    };
}
