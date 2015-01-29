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
import com.github.besherman.lifx.LFXLight;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Finds the group called "Test Group" and adds all lights to it.
 */
public class GroupEx03AddLightsToGroup {
    public static void main(String[] args) throws Exception {
        LFXClient client = new LFXClient();
        client.open(true);       
        try {
            LFXGroup group = client.getGroups().add("Test Group");

            Iterator<LFXLight> it = client.getLights().iterator();
            if(!it.hasNext()) {
                Logger.getLogger(GroupEx03AddLightsToGroup.class.getName()).log(Level.INFO, "No lights found");
                return;
            }
            
            while(it.hasNext()) {
                LFXLight light = it.next();
                if(!group.contains(light)) {
                    System.out.format("Adding light '%s' to group '%s' %n", light.getLabel(), group.getLabel());
                    group.add(light);
                }
            } 
        } finally {
            client.close();
        }
    }    
}
