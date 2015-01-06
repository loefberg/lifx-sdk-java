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

import java.util.Objects;

/**
 * The color and waveform of a light. 
 */
public class LFXWaveform {
    private final int stream;			
    private final boolean transientType;		
    private final LFXHSBKColor color;		
    private final long period;			
    private final float cycles;			
    private final short skewRatio;			
    private final int waveform;			

    /**
     * Creates a waveform with standard values.
     * @param alarmDuration the alarms duration in ms
     */
    public LFXWaveform(long alarmDuration) {
        this.stream = 0;
        this.transientType = false;
        this.color = new LFXHSBKColor(120, 0, 1, 8000);
        this.period = 2 * alarmDuration;
        this.cycles = 0.5f;
        this.skewRatio = 0;
        this.waveform = 1;
    }    

    /**
     * Creates a waveform with color and standard values.
     * @param alarmDuration the alarms duration in ms
     */    
    public LFXWaveform(LFXHSBKColor color, long alarmDuration) {
        this(0, false, color, 2 * alarmDuration, 0.5f, (short)0, 1);
    }        
    
    /**
     * Creates a waveform with custom values. Be aware that different values
     * than the default have not been tested! Use the wrong ones and the 
     * light might explode and kill you. Or not.
     */
    public LFXWaveform(int stream, boolean transienttype, LFXHSBKColor color, long period, float cycles, short skewRatio, int waveform) {
        if(color == null) {
            throw new IllegalArgumentException("color can not be null");
        }
        this.stream = stream;
        this.transientType = transienttype;
        this.color = color;
        this.period = period;
        this.cycles = cycles;
        this.skewRatio = skewRatio;
        this.waveform = waveform;
    }

    /**
     * Creates a copy of other.
     */
    public LFXWaveform(LFXWaveform other) {
        if(other == null) {
            throw new IllegalArgumentException("other can not be null");
        }
        this.stream = other.stream;
        this.transientType = other.transientType;
        this.color = other.color;
        this.period = other.period;
        this.cycles = other.cycles;
        this.skewRatio = other.skewRatio;
        this.waveform = other.waveform;        
    }

    /**
     * Unknown.
     */
    public int getStream() {
        return stream;
    }

    /**
     * Unknown.
     */
    public boolean isTransientType() {
        return transientType;
    }

    /**
     * Returns the color.
     */
    public LFXHSBKColor getColor() {
        return color;
    }

    /**
     * This has to be alarm duration (ms) * 2.
     */
    public long getPeriod() {
        return period;
    }

    /**
     * Unknown.
     */
    public float getCycles() {
        return cycles;
    }

    /**
     * Unknown.
     */
    public short getSkewRatio() {
        return skewRatio;
    }

    /**
     * Unknown.
     */
    public int getWaveform() {
        return waveform;
    }


    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + this.stream;
        hash = 17 * hash + (this.transientType ? 1 : 0);
        hash = 17 * hash + Objects.hashCode(this.color);
        hash = 17 * hash + (int) (this.period ^ (this.period >>> 32));
        hash = 17 * hash + Float.floatToIntBits(this.cycles);
        hash = 17 * hash + this.skewRatio;
        hash = 17 * hash + this.waveform;
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
        final LFXWaveform other = (LFXWaveform) obj;
        if (this.stream != other.stream) {
            return false;
        }
        if (this.transientType != other.transientType) {
            return false;
        }
        if (!Objects.equals(this.color, other.color)) {
            return false;
        }
        if (this.period != other.period) {
            return false;
        }
        if (Float.floatToIntBits(this.cycles) != Float.floatToIntBits(other.cycles)) {
            return false;
        }
        if (this.skewRatio != other.skewRatio) {
            return false;
        }
        if (this.waveform != other.waveform) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Waveform{" + "stream=" + stream + ", transienttype=" + transientType + ", color=" + color + ", period=" + period + ", cycles=" + cycles + ", skewRatio=" + skewRatio + ", waveform=" + waveform + '}';
    }
}
