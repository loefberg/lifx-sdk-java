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
package com.github.besherman.lifx.examples.groups;

import com.github.besherman.lifx.LFXClient;
import com.github.besherman.lifx.LFXGroup;
import com.github.besherman.lifx.LFXGroupCollectionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 */
public class GroupEx10PropertyListener {
    public static void main(String[] args) throws Exception {
        LFXClient client = new LFXClient();
        client.getGroups().addGroupCollectionListener(new MyGroupListener());
        client.open(false);
        try {
            Thread.sleep(120 * 1000);
        } finally {
            client.close();
        }
    }    
    
    private static class MyGroupListener implements LFXGroupCollectionListener {
        private final MyPropertyListener propertyListener = new MyPropertyListener();
        
        @Override
        public void groupAdded(LFXGroup group) {
            System.out.format("Group '%s' with %s light(s) added %n", group.getLabel(), group.size());            
            group.addPropertyChangeListener(propertyListener);
        }

        @Override
        public void groupRemoved(LFXGroup group) {
            System.out.format("Group '%s' removed %n", group.getLabel());            
            group.removePropertyChangeListener(propertyListener);
        }        
    }
    
    private static class MyPropertyListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            LFXGroup group = (LFXGroup)evt.getSource();
            System.out.format("Group changed %s from '%s' to '%s' %n", evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
        }        
    }
}
