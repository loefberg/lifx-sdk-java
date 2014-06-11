/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package android.net;

import java.net.InetAddress;

/**
 *
 * @author Richard
 */
public class DhcpInfo {
    private final String broadcastHostAddress;
    private final byte[] broadcastAddr;
    
    public DhcpInfo(InetAddress broadcast) {
        this.broadcastHostAddress = broadcast.getHostAddress();
        this.broadcastAddr = broadcast.getAddress();
    }
    
    public String getBroadcastHostAddress() {
        return broadcastHostAddress;
    }
    
    public byte[] getBroadcastAddress() {
        return broadcastAddr;
    }
}
