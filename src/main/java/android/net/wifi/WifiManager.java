/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package android.net.wifi;

import android.net.DhcpInfo;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 *
 * @author Richard
 */
public class WifiManager {
    private DhcpInfo dhcp;
    
    public DhcpInfo getDhcpInfo() {        
        if(dhcp == null) {
            dhcp = createDhcpInfo();
        }
        return dhcp;
    }
    
    private DhcpInfo createDhcpInfo() {        
        NetworkInterface iface;
        try {
            iface = getActiveIPv4Interface();
        } catch(SocketException ex) {
            throw new RuntimeException(ex);
        }
        
        for(InterfaceAddress ifaceAddr: iface.getInterfaceAddresses()) {
            InetAddress addr = ifaceAddr.getAddress();
            if(addr instanceof Inet4Address) {
                //return new DhcpInfo(pack(addr.getAddress()), ifaceAddr.getNetworkPrefixLength());
                //return new DhcpInfo(pack(addr.getAddress()), 0);
                InetAddress broadcast = ifaceAddr.getBroadcast();
                return new DhcpInfo(broadcast.getHostAddress(), broadcast.getAddress());
            }
        }        
        
        
        
        throw new RuntimeException("No network interface found");
    }
    
    private static int pack(byte[] bytes) {
        int result = 0;  
        for (byte b: bytes) {  
            result = result << 8 | (b & 0xFF);  
        }
        return result;
    }    
    
    private static NetworkInterface getActiveIPv4Interface() throws SocketException {
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
