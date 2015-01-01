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
package com.github.besherman.lifx.examples.alarm;

import com.github.besherman.lifx.LFXAlarmCollection;
import com.github.besherman.lifx.LFXClient;
import com.github.besherman.lifx.LFXLight;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class AlarmEx04ClearingAlarms {
    public static void main(String[] args) throws Exception {
        LFXClient client = new LFXClient();
        client.open(true);
        try {
            // the alarms takes a while to load
            Thread.sleep(2 * 1000);
            
            for(LFXLight light: client.getLights()) {                
                LFXAlarmCollection alarms = light.getAlarms();
                if(alarms.size() > 0) {
                    for(int i = 0; i < alarms.size(); i++) {
                        System.out.format("Clearing alarm %s for light '%s' (%s) %n", i, light.getLabel(), light.getID());
                        alarms.clear(i);
                    }
                } else {
                    Logger.getLogger(AlarmEx04ClearingAlarms.class.getName()).log(Level.INFO, "Alarms was not loaded");
                }
            }            
        } finally {
            client.close();
        }
    }    

}
