/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package android.content;

import android.net.wifi.WifiManager;

/**
 *
 * @author Richard
 */
public class Context {
    public static final String WIFI_SERVICE = "wifi";
    
    private final WifiManager wifiMgr = new WifiManager();
    
    public Context() {
    }
    
    public Object getSystemService(String service) {
        if(!WIFI_SERVICE.equals(service)) {
            throw new RuntimeException("Unknown service: " + service);
        }
        return wifiMgr;
    }
}
