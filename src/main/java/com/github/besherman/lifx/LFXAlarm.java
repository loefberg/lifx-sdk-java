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

import java.util.Date;
import java.util.Objects;

/**
 * Specifies an alarm for a light. 
 */
public class LFXAlarm {    
    private static final long DEFAULT_DURATION = 250;
    
    private final Date time;
    private final boolean power;
    private final long duration;
    private final LFXWaveform waveform;

    /**
     * Creates an alarm with standard values.
     */
    public LFXAlarm() {
        this.time = new Date();
        this.power = true;
        this.duration = DEFAULT_DURATION;
        this.waveform = new LFXWaveform(DEFAULT_DURATION);
    }
    
    /**
     * Creates an alarm that will turn on the light at the given time.
     */
    public LFXAlarm(Date time) {
        this(time, true, DEFAULT_DURATION, new LFXWaveform(DEFAULT_DURATION));
    }

    /**
     * Creates an alarm that will turn on the light at the given time with
     * color.
     */
    public LFXAlarm(Date time, LFXHSBKColor color) {
        this(time, true, DEFAULT_DURATION, new LFXWaveform(color, DEFAULT_DURATION));
    }

    /**
     * Creates an alarm that will turn on the light at the given time and
     * fade to the color.
     * @param time when the alarm will trigger
     * @param color the color to set the light to
     * @param duration fade time in milliseconds.
     */
    public LFXAlarm(Date time, LFXHSBKColor color, long duration) {
        this(time, true, duration, new LFXWaveform(color, duration));
    }
    
    /**
     * Creates an alarm that will turn the light on or of at the given time.
     */
    public LFXAlarm(Date time, boolean power) {
        this(time, power, DEFAULT_DURATION, new LFXWaveform(DEFAULT_DURATION));
    }    
    
    /**
     * Creates an alarm that will turn the light on or of at a given time
     * and fading to the given color.
     * 
     * @param time the time when the alarm will trigger.
     * @param power if true the light will be turned on
     * @param duration the fade duration in milliseconds
     * @param color the color to set the light to
     */
    public LFXAlarm(Date time, boolean power, long duration, LFXHSBKColor color) {
        this(time, power, duration, new LFXWaveform(color, duration));
    }

    /**
     * Creates an alarm that will turn the light on or of at a given time
     * and fading to the given waveform.
     * 
     * @param time the time when the alarm will trigger.
     * @param power if true the light will be turned on
     * @param duration the fade duration in milliseconds
     * @param waveform the waveform to set the light to
     */
    public LFXAlarm(Date time, boolean power, long duration, LFXWaveform waveform) {
        if(time == null) {
            throw new IllegalArgumentException("date can not be null");
        }
        if(waveform == null) {
            throw new IllegalArgumentException("waveform can not be null");
        }
        this.time = time;
        this.power = power;
        this.duration = duration;
        this.waveform = waveform;
    }

    /**
     * Creates a copy of other.
     */
    public LFXAlarm(LFXAlarm other) {
        if(other == null) {
            throw new IllegalArgumentException("other can not be null");
        }
        this.time = new Date(other.time.getTime());
        this.power = other.power;
        this.duration = other.duration;
        this.waveform = new LFXWaveform(other.waveform);
    }
    
    /**
     * Creates a copy of this alarm with a different time.
     */
    public LFXAlarm reschedule(Date newTime) {
        return new LFXAlarm(newTime, power, duration, waveform);
    }

    /**
     * Returns the date when the alarm is to go off.
     */
    public Date getTime() {
        return time;
    }

    /**
     * Returns true if the light will be turned on when the alarm is triggered.
     */
    public boolean isPower() {
        return power;
    }

    /**
     * Returns the fade duration in milliseconds. The fade duration is the 
     * time from the light starts to change to the time that the light has
     * reached the indicated color.
     */
    public long getDuration() {
        return duration;
    }

    /**
     * Returns the light's waveform. 
     */
    public LFXWaveform getWaveform() {
        return waveform;
    }

    /**
     * Returns the color that the alarm will set. This method delegates to
     * the waveform's color.
     */
    public LFXHSBKColor getColor() {
        return waveform.getColor();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.time);
        hash = 53 * hash + (this.power ? 1 : 0);
        hash = 53 * hash + (int) (this.duration ^ (this.duration >>> 32));
        hash = 53 * hash + Objects.hashCode(this.waveform);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LFXAlarm other = (LFXAlarm) obj;
        if (!Objects.equals(this.time, other.time)) {
            return false;
        }
        if (this.power != other.power) {
            return false;
        }
        if (this.duration != other.duration) {
            return false;
        }
        if (!Objects.equals(this.waveform, other.waveform)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Alarm{" + "time=" + time + ", power=" + power + ", duration=" + duration + ", waveform=" + waveform + '}';
    }
}
