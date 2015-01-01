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
package com.github.besherman.lifx.impl.light;


import com.github.besherman.lifx.LFXInterfaceFirmware;
import com.github.besherman.lifx.LFXInterfaceStat;
import com.github.besherman.lifx.LFXLightDetails;
import com.github.besherman.lifx.LFXVersion;
import com.github.besherman.lifx.impl.entities.internal.LFXBinaryTypes;
import com.github.besherman.lifx.impl.entities.internal.LFXMessage;
import com.github.besherman.lifx.impl.entities.internal.LFXTarget;
import com.github.besherman.lifx.impl.entities.internal.structle.LxProtocol;
import com.github.besherman.lifx.impl.entities.internal.structle.LxProtocolDevice;
import com.github.besherman.lifx.impl.entities.internal.structle.LxProtocolLight;
import com.github.besherman.lifx.impl.entities.internal.structle.StructleTypes.UInt32;
import com.github.besherman.lifx.impl.entities.internal.structle.StructleTypes.UInt64;
import com.github.besherman.lifx.impl.network.LFXMessageRouter;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Richard
 */
public class LFXLightDetailsImpl implements LFXLightDetails {
    private final LFXTarget target;
    private final LFXMessageRouter router;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    private float temperature;
    private long uptime;
    private long downtime;
    private int resetSwitchPosition;    
    
    private LFXInterfaceStat wifiStat;
    private LFXInterfaceStat meshStat;
    
    private LFXInterfaceFirmware wifiFirmware;
    private LFXInterfaceFirmware meshFirmware;
    
    private float mcuRailVoltage;
    
    private Collection<LFXVersion> versions = new ArrayList<>();    
    
    private final Set<LxProtocol.Type> messagesUntilLoaded = Collections.synchronizedSet(new HashSet<>(Arrays.asList(
            // wait for the basic information is loaded
            LxProtocol.Type.LX_PROTOCOL_LIGHT_STATE,
            LxProtocol.Type.LX_PROTOCOL_DEVICE_STATE_LABEL,
            LxProtocol.Type.LX_PROTOCOL_DEVICE_STATE_POWER,
            LxProtocol.Type.LX_PROTOCOL_DEVICE_STATE_TIME,
    
            // and until the details are loaded as well    
            LxProtocol.Type.LX_PROTOCOL_LIGHT_STATE_TEMPERATURE, 
            LxProtocol.Type.LX_PROTOCOL_DEVICE_STATE_INFO, 
            LxProtocol.Type.LX_PROTOCOL_DEVICE_STATE_RESET_SWITCH, 
            LxProtocol.Type.LX_PROTOCOL_DEVICE_STATE_MESH_INFO, 
            LxProtocol.Type.LX_PROTOCOL_DEVICE_STATE_MESH_FIRMWARE, 
            LxProtocol.Type.LX_PROTOCOL_DEVICE_STATE_WIFI_INFO, 
            LxProtocol.Type.LX_PROTOCOL_DEVICE_STATE_WIFI_FIRMWARE, 
            LxProtocol.Type.LX_PROTOCOL_DEVICE_STATE_VERSION, 
            LxProtocol.Type.LX_PROTOCOL_DEVICE_STATE_MCU_RAIL_VOLTAGE    
    )));
    

    public LFXLightDetailsImpl(LFXMessageRouter router, LFXTarget target) {
        this.router = router;      
        this.target = target;
    }
    
    @Override
    public float getTemperature() {
        return temperature;
    }

    @Override
    public long getUptime() {
        return uptime;
    }

    @Override
    public long getDowntime() {
        return downtime;
    }

    @Override
    public int getResetSwitchPosition() {
        return resetSwitchPosition;
    }

    @Override
    public LFXInterfaceStat getMeshStat() {
        return meshStat;
    }

    @Override
    public LFXInterfaceStat getWifiStat() {
        return wifiStat;
    }

    @Override
    public LFXInterfaceFirmware getMeshFirmware() {
        return meshFirmware;
    }

    @Override
    public LFXInterfaceFirmware getWifiFirmware() {
        return wifiFirmware;
    }

    @Override
    public Collection<LFXVersion> getVersions() {
        return Collections.unmodifiableCollection(versions);
    }    

    @Override
    public float getMCURailVoltage() {
        return mcuRailVoltage;
    }

    @Override
    public void load() {
        router.sendMessage(new LFXMessage(LxProtocol.Type.LX_PROTOCOL_LIGHT_GET_TEMPERATURE, target));
        router.sendMessage(new LFXMessage(LxProtocol.Type.LX_PROTOCOL_DEVICE_GET_INFO, target));
        router.sendMessage(new LFXMessage(LxProtocol.Type.LX_PROTOCOL_DEVICE_GET_RESET_SWITCH, target));
        router.sendMessage(new LFXMessage(LxProtocol.Type.LX_PROTOCOL_DEVICE_GET_MESH_INFO, target));
        router.sendMessage(new LFXMessage(LxProtocol.Type.LX_PROTOCOL_DEVICE_GET_MESH_FIRMWARE, target));
        router.sendMessage(new LFXMessage(LxProtocol.Type.LX_PROTOCOL_DEVICE_GET_WIFI_INFO, target));
        router.sendMessage(new LFXMessage(LxProtocol.Type.LX_PROTOCOL_DEVICE_GET_WIFI_FIRMWARE, target));
        router.sendMessage(new LFXMessage(LxProtocol.Type.LX_PROTOCOL_DEVICE_GET_VERSION, target));
        router.sendMessage(new LFXMessage(LxProtocol.Type.LX_PROTOCOL_DEVICE_GET_MCU_RAIL_VOLTAGE, target));        
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    @Override
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener l) {
        pcs.addPropertyChangeListener(propertyName, l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }
    
    public void close() {    
    }
    
    public boolean isLoaded() {
        return messagesUntilLoaded.isEmpty();
    }    
    
    public void handleMessage(LFXMessage message) {
        switch (message.getType()) {
            case LX_PROTOCOL_LIGHT_STATE_TEMPERATURE: {
                LxProtocolLight.StateTemperature payload = message.getPayload();
                temperatureDidChangeTo(payload.getTemperature().getValue());
                break;
            }
            case LX_PROTOCOL_DEVICE_STATE_INFO: {
                LxProtocolDevice.StateInfo payload = message.getPayload();                                
                infoDidChangeTo(payload.getUptime().getBigIntegerValue(), payload.getDowntime().getBigIntegerValue());                
                break;
            }
            case LX_PROTOCOL_DEVICE_STATE_RESET_SWITCH: {
                LxProtocolDevice.StateResetSwitch payload = message.getPayload();
                resetSwitchDidChangeTo(payload.getPosition().getValue());
                break;
            }
            case LX_PROTOCOL_DEVICE_STATE_MESH_INFO: {
                LxProtocolDevice.StateMeshInfo payload = message.getPayload();
                meshInfoDidChangeTo(payload.getSignal().getValue(), 
                        payload.getTx().getValue(), payload.getRx().getValue(), 
                        payload.getMcu_temperature().getValue());
                break;
            }
            case LX_PROTOCOL_DEVICE_STATE_MESH_FIRMWARE: {
                LxProtocolDevice.StateMeshFirmware payload = message.getPayload();
                meshFirmwareDidChangeTo(payload.getBuild(),
                        payload.getInstall(),
                        payload.getVersion());
                break;
            }
            case LX_PROTOCOL_DEVICE_STATE_WIFI_INFO: {
                LxProtocolDevice.StateWifiInfo payload = message.getPayload();
                wifiInfoDidChangeTo(payload.getSignal().getValue(), 
                        payload.getTx().getValue(), payload.getRx().getValue(), 
                        payload.getMcu_temperature().getValue());
                break;
            }
            case LX_PROTOCOL_DEVICE_STATE_WIFI_FIRMWARE: {
                LxProtocolDevice.StateWifiFirmware payload = message.getPayload();                
                wifiFirmwareDidChangeTo(payload.getBuild(),
                        payload.getInstall(),
                        payload.getVersion());
                break;
            }   
            case LX_PROTOCOL_DEVICE_STATE_VERSION: {
                LxProtocolDevice.StateVersion payload = message.getPayload();
                versionDidChangeTo(payload.getProduct().getValue(),
                        payload.getVendor().getValue(),
                        payload.getVersion().getValue());
                break;
            }
            case LX_PROTOCOL_DEVICE_STATE_MCU_RAIL_VOLTAGE: {
                LxProtocolDevice.StateMcuRailVoltage payload = message.getPayload();
                mcuRailVoltageDidChangeTo(payload.getVoltage().getValue());
                break;
            }
            default:
                break;
        }
        
        messagesUntilLoaded.remove(message.getType());
    }    
    
    private void resetSwitchDidChangeTo(int newPosition) {
        long oldValue = resetSwitchPosition;
        resetSwitchPosition = newPosition;
        pcs.firePropertyChange("resetSwitchPosition", oldValue, resetSwitchPosition);
    }
    
    private void temperatureDidChangeTo(int newTemp) {
        float oldTemp = temperature;
        temperature = newTemp / 100f;
        pcs.firePropertyChange("temperature", oldTemp, temperature);
    }
    
    private void infoDidChangeTo(BigInteger newUptime, BigInteger newDowntime) {        
        long oldUptime = uptime;        
        uptime = newUptime.divide(new BigInteger("1000000")).longValue();        
        pcs.firePropertyChange("uptime", oldUptime, uptime);
        
        long oldDowntime = downtime;
        downtime = newDowntime.divide(new BigInteger("1000000")).longValue();
        pcs.firePropertyChange("downtime", oldDowntime, downtime);
    }
    
    private void meshInfoDidChangeTo(float signal, long tx, long rx, int mcuTemperature) {
        LFXInterfaceStat old = meshStat;
        meshStat = new LFXInterfaceStat(signal, tx, rx, mcuTemperature);
        pcs.firePropertyChange("meshStat", old, meshStat);
    }
    
    private void wifiInfoDidChangeTo(float signal, long tx, long rx, int mcuTemperature) {
        LFXInterfaceStat old = wifiStat;
        wifiStat = new LFXInterfaceStat(signal, tx, rx, mcuTemperature);
        pcs.firePropertyChange("wifiStat", old, wifiStat);
    }    
    
    private void meshFirmwareDidChangeTo(UInt64 build, UInt64 install, UInt32 version) {
        LFXInterfaceFirmware old = meshFirmware;
        meshFirmware = LFXBinaryTypes.createFirmware(build, install, version);
        pcs.firePropertyChange("meshFirmware", old, meshFirmware);
    }
    
    private void wifiFirmwareDidChangeTo(UInt64 build, UInt64 install, UInt32 version) {
        LFXInterfaceFirmware old = wifiFirmware;
        wifiFirmware = LFXBinaryTypes.createFirmware(build, install, version);
        pcs.firePropertyChange("wifiFirmware", old, wifiFirmware);
    }
    
    private void versionDidChangeTo(long newProduct, long newVendor, long newVersion) {
        LFXVersion version = new LFXVersion(newProduct, newVendor, newVersion);
        if(!versions.contains(version)) {
            Collection<LFXVersion> old = versions;
            Collection<LFXVersion> newVersions = new ArrayList<>(old);
            newVersions.add(version);
            versions = newVersions;
            pcs.firePropertyChange("versions", old, versions);
        }
    }
    
    private void mcuRailVoltageDidChangeTo(long newVoltage) {
        float old = mcuRailVoltage;
        mcuRailVoltage = newVoltage / 1000f;
        pcs.firePropertyChange("mcuRailVoltage", old, mcuRailVoltage);
    }    
}
