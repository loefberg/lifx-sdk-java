/*
 * The MIT License
 *
 * Copyright 2014 Richard.
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
package com.github.besherman.lifx.examples.lights;

import com.github.besherman.lifx.LFXClient;
import com.github.besherman.lifx.LFXLight;
import com.github.besherman.lifx.LFXLightCollectionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Listens to new lights and when found adds a property changed listener which
 * prints all properties when they changes.
 * 
 * Give it some time.
 */
public class LightEx06PropertyListener {
    public static void main(String[] args) throws Exception {
        LFXClient client = new LFXClient();        
        client.getLights().addLightCollectionListener(new MyLightListener());
        client.open(false);
        try {
            Thread.sleep(120 * 1000);
        } finally {
            client.close();
        }
    }    
    
    private static class MyLightListener implements LFXLightCollectionListener {
        private final MyPropertyListener propertyListener = new MyPropertyListener();
        
        @Override
        public void lightAdded(LFXLight light) {
            System.out.format("Light added, label '%s' and id '%s' %n", light.getLabel(), light.getID());
            light.addPropertyChangeListener(propertyListener);
        }

        @Override
        public void lightRemoved(LFXLight light) {
            System.out.format("Light removed, label '%s' and id '%s' %n", light.getLabel(), light.getID());
            light.removePropertyChangeListener(propertyListener);
        }
    }
    
    private static class MyPropertyListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            LFXLight light = (LFXLight)evt.getSource();
            System.out.format("Light %s changed %s from '%s' to '%s' %n", light.getID(), evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
        }        
    }    
}
