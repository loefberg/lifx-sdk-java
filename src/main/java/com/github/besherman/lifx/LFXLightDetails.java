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
package com.github.besherman.lifx;

import java.beans.PropertyChangeListener;
import java.util.Collection;

/**
 * Detailed information about a light. The lights are periodically updated
 * but the details is only loaded once when the light is discovered. To update
 * the details call {@link LFXLightDetails#load()}.
 */
public interface LFXLightDetails {
    /**
     * Asynchronously loads the alarms from the light.
     */
    void load();

    /**
     * Returns the lights temperature in degrees Celsius.
     */
    float getTemperature();
    
    /**
     * Returns the uptime in milliseconds.
     */
    long getUptime();

    /**
     * Returns the downtime in milliseconds.
     */
    long getDowntime();
    
    /**
     * Returns the position of the reset switch.
     */
    int getResetSwitchPosition();

    /**
     * Returns information about the mesh interface.
     */
    LFXInterfaceStat getMeshStat();
    
    /**
     * Returns information about the wifi interface.
     */
    LFXInterfaceStat getWifiStat();    
    
    /**
     * Returns information about the mesh firmware.
     */
    LFXInterfaceFirmware getMeshFirmware();
    
    /**
     * Returns information about the wifi firmware.
     */
    LFXInterfaceFirmware getWifiFirmware();

    /**
     * Returns information for the different products in the light.
     */
    Collection<LFXVersion> getVersions();

    /**
     * Returns the MCU rail voltage.
     */
    float getMCURailVoltage();
    
    void addPropertyChangeListener(PropertyChangeListener l);
    void addPropertyChangeListener(String propertyName, PropertyChangeListener l);
    void removePropertyChangeListener(PropertyChangeListener l);    
}
