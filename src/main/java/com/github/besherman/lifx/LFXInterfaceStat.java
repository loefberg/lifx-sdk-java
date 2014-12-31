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


/**
 * Interface status.
 */
public class LFXInterfaceStat {
    private final float signal;
    private final long tx;
    private final long rx;
    private final int mcuTemperature;

    public LFXInterfaceStat(float signal, long tx, long rx, int mcuTemperature) {
        this.signal = signal;
        this.tx = tx;
        this.rx = rx;
        this.mcuTemperature = mcuTemperature;
    }
    
    public float getSignal() {
        return signal;
    }

    public long getTx() {
        return tx;
    }

    public long getRx() {
        return rx;
    }

    public int getMcuTemperature() {
        return mcuTemperature;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Float.floatToIntBits(this.signal);
        hash = 53 * hash + (int) (this.tx ^ (this.tx >>> 32));
        hash = 53 * hash + (int) (this.rx ^ (this.rx >>> 32));
        hash = 53 * hash + this.mcuTemperature;
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
        final LFXInterfaceStat other = (LFXInterfaceStat) obj;
        if (Float.floatToIntBits(this.signal) != Float.floatToIntBits(other.signal)) {
            return false;
        }
        if (this.tx != other.tx) {
            return false;
        }
        if (this.rx != other.rx) {
            return false;
        }
        if (this.mcuTemperature != other.mcuTemperature) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "InterfaceStat{" + "signal=" + signal + ", tx=" + tx + ", rx=" + rx + ", mcuTemperature=" + mcuTemperature + '}';
    }
}
