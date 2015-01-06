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
import java.util.Iterator;

/**
 * A collection of alarms for a light. The protocol allows for 256 different
 * alarms but the current firmware (1.5) only supports 2 alarms. 
 * See {@link LFXAlarmCollection#size()} for the number of alarms supported. 
 * Note that the light gives a reference to its alarm collection before the 
 * collection is guaranteed to be loaded so if {@link LFXAlarmCollection#size()} 
 * is 0 it may still be loading.
 * 
 * The alarm collection is only loaded once when the light is discovered. 
 * To update the alarms call {@link LFXAlarmCollection#load()}.
 */
public interface LFXAlarmCollection extends Iterable<LFXAlarm> {
    /**
     * Returns an iterator over the alarms in the collection.
     */
    @Override
    Iterator<LFXAlarm> iterator();

    /**
     * Returns the number of alarms in the collection.
     */
    int size();

    /**
     * Returns the alarm at the given index.
     */
    LFXAlarm get(int index);

    /**
     * Sets the alarm at the given index.
     */
    void set(int index, LFXAlarm alarm);

    /**
     * Clears the alarm at the given index.
     */
    void clear(int index);

    /**
     * Asynchronously loads the alarms from the light.
     */
    void load();
    
    void addPropertyChangeListener(PropertyChangeListener l);
    void addPropertyChangeListener(String propertyName, PropertyChangeListener l);
    void removePropertyChangeListener(PropertyChangeListener l);        
}
