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
    private final String broadcast;
    private final byte[] broadcastAddr;
    
    public DhcpInfo(String broadcast, byte[] broadcastAddr) {
        this.broadcast = broadcast;
        this.broadcastAddr = broadcastAddr;
    }
    
    public String getBroadcast() {
        return broadcast;
    }
    
    public byte[] getBroadcastAddress() {
        return broadcastAddr;
    }
    
    
//    public final int ipAddress;
//    public final int netmask;
//
//    public DhcpInfo(int ipAddress, int netmask) {
//        this.ipAddress = ipAddress;
//        this.netmask = netmask;
//    }



}
