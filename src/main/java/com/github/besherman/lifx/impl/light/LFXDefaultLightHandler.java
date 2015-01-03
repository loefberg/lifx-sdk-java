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
import com.github.besherman.lifx.impl.entities.internal.LFXDeviceID;
import com.github.besherman.lifx.impl.entities.internal.LFXMessage;
import com.github.besherman.lifx.impl.entities.internal.LFXTarget;
import com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol;
import com.github.besherman.lifx.impl.entities.internal.structle.LxProtocolDevice;
import com.github.besherman.lifx.impl.network.LFXLightHandler;
import com.github.besherman.lifx.impl.network.LFXMessageRouter;
import com.github.besherman.lifx.impl.network.LFXTimerQueue;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Richard
 */
public class LFXDefaultLightHandler implements LFXLightHandler {
    private LFXMessageRouter router;
    private final LFXAllLights lights = new LFXAllLights();
    private final LFXAllGroups groups = new LFXAllGroups();
    private final CountDownLatch routerLatch = new CountDownLatch(1);
    
    private LFXTimerQueue timerQueue;

    public LFXDefaultLightHandler() {
        groups.setLights(lights);
    }    
    
    public LFXAllLights getLights() {
        return lights;
    }
    
    public LFXAllGroups getGroups() {
        return groups;
    }    
    
    public boolean waitForLoaded(long timeout, TimeUnit unit) throws InterruptedException {
        routerLatch.await(5, TimeUnit.SECONDS);
        if(router == null) {
            throw new IllegalStateException("never got a MessageRouter");
        }
        
        // wait for the first PAN to be sighted, this should happen fairly
        // quickly
        boolean sucess = router.waitForInitPAN(2, TimeUnit.SECONDS);
        if(sucess) {
            sucess = lights.waitForInitLoaded(timeout, unit);
        } 
        if(sucess) {
            // TODO: recalculate the timeout
            return groups.waitForInitLoaded(timeout, unit);
        }
        return false;
    }
    
    @Override
    public void setRouter(LFXMessageRouter router) {        
        this.router = router;
        groups.setRouter(router);          
        routerLatch.countDown();
    }    
    
    @Override
    public void handleMessage(Set<LFXDeviceID> targets, LFXMessage message) {
        lights.handleMessage(router, timerQueue, targets, message);
        groups.handleMessage(targets, message);
    }

    @Override
    public void open() {
        timerQueue = new LFXTimerQueue();        
        
        timerQueue.doLater(sendGetGroupLabelsAction, 1, TimeUnit.SECONDS);
        
        timerQueue.doRepeatedly(sendGetLightInfo, 15, TimeUnit.SECONDS);        
        timerQueue.doRepeatedly(refreshLightsAction, 1, TimeUnit.SECONDS);
        
        
//        timerQueue.doRepeatedly(new Runnable() {
//            @Override
//            public void run() {
//                lights.printReasons();
//            }
//        }, 1, TimeUnit.SECONDS);
    }

    @Override
    public void close() {
        timerQueue.close();        
    }
    
    /**
     * Action that asks all lights about their basic info (what is avaliable directly in the LFXLight class)
     * This is sent as a broadcast so we should get a response from all lights on the network.
     */
    private final Runnable sendGetLightInfo = new Runnable() {
        @Override
        public void run() {
            router.sendMessage(new LFXMessage(LxProtocol.Type.LX_PROTOCOL_DEVICE_GET_LABEL, LFXTarget.getBroadcastTarget()));
            router.sendMessage(new LFXMessage(LxProtocol.Type.LX_PROTOCOL_DEVICE_GET_POWER, LFXTarget.getBroadcastTarget()));
            router.sendMessage(new LFXMessage(LxProtocol.Type.LX_PROTOCOL_DEVICE_GET_TIME, LFXTarget.getBroadcastTarget()));
            
            // get the tag labels
            {
                Set<LFXTagID> allTags = EnumSet.allOf(LFXTagID.class);
                LxProtocolDevice.GetTagLabels payload = new LxProtocolDevice.GetTagLabels(LFXTagID.pack(allTags));
                // TODO: is it correct to do this as a broadcast? maybee just ask one
                LFXMessage msg = new LFXMessage(LxProtocol.Type.LX_PROTOCOL_DEVICE_GET_TAG_LABELS, LFXTarget.getBroadcastTarget(), payload);
                router.sendMessage(msg);                
            }
        }        
    };
    
    /**
     * Action that keeps the lights collection updated. It checks for lights
     * that has not been seen for a while and removes them.
     */
    private final Runnable refreshLightsAction = new Runnable() {
        @Override
        public void run() {
            lights.removeLostLights();
        }
        
    };
    
    /**
     * Action that makes sure that the tag labels are loaded. Keeps scheduleing
     * itself until the labels are loaded.
     * 
     * TODO: This is a bit stupid, we are spaming the network with messages.
     *       When we send this message the lights seems to ignore it in favour
     *       of other messages, so it is not until the message queue is empty
     *       that the lights even care - how do we do this better?
     */
    private final Runnable sendGetGroupLabelsAction = new Runnable() {
        @Override
        public void run() {            
            if(!groups.isLoaded()) {
                if(!lights.isEmpty()) {                    
                    router.sendMessage(new LFXMessage(LxProtocol.Type.LX_PROTOCOL_DEVICE_GET_TAG_LABELS, LFXTarget.getBroadcastTarget()));
                }
                timerQueue.doLater(sendGetGroupLabelsAction, 1, TimeUnit.SECONDS);
            }
        }
        
    };
    
}
