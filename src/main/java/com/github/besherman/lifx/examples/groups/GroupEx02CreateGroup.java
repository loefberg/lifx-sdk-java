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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates a group called "Test Group".
 */
public class GroupEx02CreateGroup {
    public static void main(String[] args) throws Exception {
        LFXClient client = new LFXClient();
        client.open(true);          
        try {        
            LFXGroup group = client.getGroups().add("Test Group");
            if(group != null) {
                System.out.format("Created group %s %n", group.getLabel());
            } else {
                Logger.getLogger(GroupEx02CreateGroup.class.getName()).log(Level.SEVERE, 
                        "Failed to create group");
            }
        } finally {
            client.close();
        }
    }    
}
