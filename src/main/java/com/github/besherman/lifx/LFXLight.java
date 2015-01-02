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

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.util.Date;

/**
 * A bulb. 
 */
public interface LFXLight {
    /**
     * Returns the bulb's unique identifier. This is the bulb's MAC address
     * as a hex string, so it will never change and always be unique for this 
     * bulb.
     */
    String getID(); 
    
    /**
     * Returns true if the light is turned on.
     */
    boolean isPower();
    
    /**
     * Turns the light on or off.
     */
    void setPower(boolean power);

    /**
     * Returns the lights label.
     */
    String getLabel();
    
    /**
     * Sets the lights label.
     * @param label can not be empty or longer than 32 characters.
     */
    void setLabel(String label);
    
    /**
     * Returns the lights color.
     */
    LFXHSBKColor getColor();
    
    /**
     * Sets the lights color.
     */
    void setColor(LFXHSBKColor color);
    
    /**
     * Sets the lights color over time.
     * @param color the color to set.
     * @param duration the fade time in milliseconds.
     */
    void setColor(LFXHSBKColor color, long duration);
    
    /**
     * Sets the lights color.
     */
    void setColor(Color color);
    
    /**
     * Sets the lights color over time.
     * @param color the color to set.
     * @param duration the fade time in milliseconds.
     */
    void setColor(Color color, long duration);
    
    /**
     * Returns the lights current time.
     */
    Date getTime();

    /**
     * Returns the lights alarms. 
     */
    LFXAlarmCollection getAlarms();

    /**
     * Returns detailed information about the light. 
     */
    LFXLightDetails getDetails();
    
    void addPropertyChangeListener(PropertyChangeListener l);
    void addPropertyChangeListener(String propertyName, PropertyChangeListener l);
    void removePropertyChangeListener(PropertyChangeListener l);
}
