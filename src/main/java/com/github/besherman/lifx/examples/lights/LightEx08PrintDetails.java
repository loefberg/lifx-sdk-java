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
import com.github.besherman.lifx.LFXLightCollection;
import com.github.besherman.lifx.LFXLightDetails;
import com.github.besherman.lifx.LFXVersion;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 */
public class LightEx08PrintDetails {
    public static void main(String[] args) throws Exception {
        LFXClient client = new LFXClient();  
        Timer timer = new Timer();
        TimerTask task = new MyTask(client.getLights());        
        
        timer.schedule(task, 1000, 15000);
        
        client.open(false);
        try {
            Thread.sleep(120 * 1000);
        } finally {
            client.close();
        }
        timer.cancel();
    }
    
    private static class MyTask extends TimerTask {
        private final LFXLightCollection lights;

        public MyTask(LFXLightCollection lights) {
            this.lights = lights;
        }
        
        @Override
        public void run() {
            for(LFXLight light: lights) {
                printDetails(light);
            }
        }

        private void printDetails(LFXLight light) {
            LFXLightDetails details = light.getDetails();
            
            System.out.format("light '%s' (%s) %n", light.getLabel(), light.getID());
            System.out.format("\ttemperatur=%s C %n", details.getTemperature());
            System.out.format("\tuptime=%s ms %n", details.getUptime());
            System.out.format("\tdowntime=%s ms %n", details.getDowntime());
            System.out.format("\tMCU Rail Voltage=%s V %n", details.getMCURailVoltage());
            System.out.format("\treset switch position=%s %n", details.getResetSwitchPosition());            

            System.out.println("\tMesh:");
            System.out.format("\t\tmcu temp=%s %n", details.getMeshStat().getMcuTemperature());
            System.out.format("\t\trx=%s %n", details.getMeshStat().getRx());
            System.out.format("\t\ttx=%s %n", details.getMeshStat().getTx());
            System.out.format("\t\tsignal=%s %n", details.getMeshStat().getSignal());
            
            System.out.println("\tWifi:");
            System.out.format("\t\tmcu temp=%s %n", details.getWifiStat().getMcuTemperature());
            System.out.format("\t\trx=%s %n", details.getWifiStat().getRx());
            System.out.format("\t\ttx=%s %n", details.getWifiStat().getTx());
            System.out.format("\t\tsignal=%s %n", details.getWifiStat().getSignal());
            
            for(LFXVersion version: details.getVersions()) {
                System.out.println("\tVersion:");
                System.out.format("\t\tproduct=%s %n", version.getProduct());
                System.out.format("\t\tvendor=%s %n", version.getVendor());
                System.out.format("\t\tversion=%s %n", version.getVersion());
            }
            System.out.println("");
            
            details.load();            
        }
        
        
    }
    
}
