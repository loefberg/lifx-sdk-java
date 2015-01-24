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
import com.github.besherman.lifx.LFXHSBKColor;
import com.github.besherman.lifx.LFXLight;
import com.github.besherman.lifx.LFXLightCollection;
import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Turns on all lights and goes through different color temperatures.
 */
public class LightEx13ChangeBrightness {
    public static void main(String[] args) throws Exception {
        LFXClient client = new LFXClient();        
        Timer timer = new Timer();
        client.open(false);
        try {
            timer.scheduleAtFixedRate(new Task(client.getLights()), 0, 500);
            Thread.sleep(60 * 1000);
            for(LFXLight light: client.getLights()) {
                light.setPower(false);
            }
        } finally {
            timer.cancel();
            client.close();
        }
    }    
    
    private static class Task extends TimerTask {
        private final LFXLightCollection lights;
        private float brightness = 0;

        public Task(LFXLightCollection lights) {
            this.lights = lights;
        }
        
        @Override
        public void run() {
            try {
                brightness += 0.1;

                if(brightness > 1) {
                    brightness = 0;
                }
                System.out.println(brightness);                
                for(LFXLight light: lights) {
                    if(!light.isPower()) {
                        light.setPower(true);
                    }
                    light.setBrightness(brightness);
                }
            } catch(Exception ex) {
                Logger.getLogger(Task.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
}
