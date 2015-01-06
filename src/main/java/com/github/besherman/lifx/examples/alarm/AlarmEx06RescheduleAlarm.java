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

import com.github.besherman.lifx.LFXAlarm;
import com.github.besherman.lifx.LFXAlarmCollection;
import com.github.besherman.lifx.LFXClient;
import com.github.besherman.lifx.LFXLight;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class AlarmEx06RescheduleAlarm {
    public static void main(String[] args) throws Exception {
        LFXClient client = new LFXClient();
        client.open(true);
        try {            
            LFXLight light = client.getLights().getLightByLabel("Bulb #2");
            if(light == null) {
                Logger.getLogger(AlarmEx06RescheduleAlarm.class.getName()).log(Level.INFO, "No light found");
                return;
            }
            Date time = new Date(System.currentTimeMillis() + 5 * 1000);

            LFXAlarmCollection alarms = light.getAlarms();    
            LFXAlarm oldAlarm = alarms.get(0);
            LFXAlarm newAlarm = oldAlarm.reschedule(time);
            alarms.set(0, newAlarm);                                            
        } finally {
            client.close();
        }
    }    

}
