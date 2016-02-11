/*
 * The MIT License
 *
 * Copyright 2014 Richard Löfberg.
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

package com.github.besherman.lifx.impl.network;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 *
 * @author Richard
 */
public class LFXNetworkSettings {
    private InetSocketAddress broadcastAddress;
    
    private final int broadcastPort = 56700;
    private final int peerToPeerPort = 56750;
    private final String broadcastAddressString;
    
    public LFXNetworkSettings(String broadcastAddressString) {
        this.broadcastAddressString = broadcastAddressString;
    }

    /**
     * Returns the broadcast address that sockets should use.
     */
    public synchronized InetSocketAddress getBroadcast() throws SocketException, UnknownHostException {
        if(broadcastAddress == null) {
            if (broadcastAddressString == null) {
                broadcastAddress = new InetSocketAddress(getFirstActiveBroadcast(), broadcastPort);
            }
            else {
                InetAddress addr = InetAddress.getByName(broadcastAddressString);
                broadcastAddress = new InetSocketAddress(addr, broadcastPort);
            }
        }
        return broadcastAddress;
    }
    
  
    
    private static InetAddress getFirstActiveBroadcast() throws SocketException {
        NetworkInterface iface = getFirstActiveIPv4Interface();

        if (iface != null) {
            for (InterfaceAddress ifaceAddr : iface.getInterfaceAddresses()) {
                InetAddress addr = ifaceAddr.getAddress();
                if (addr instanceof Inet4Address) {
                    return ifaceAddr.getBroadcast();
                }
            }
        }

        return null;
    }
    
    private static NetworkInterface getFirstActiveIPv4Interface() throws SocketException {
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while(networkInterfaces.hasMoreElements()) {
            NetworkInterface iface = networkInterfaces.nextElement();
            if(iface.isUp() && !iface.isLoopback()) {
                for(InterfaceAddress ifaceAddr: iface.getInterfaceAddresses()) {
                    if(ifaceAddr.getAddress() instanceof Inet4Address) {
                        return iface;
                    }
                }
            }
        }        
        return null;
    }
    

}
