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

import java.math.BigInteger;
import java.util.Date;
import java.util.Objects;


/**
 * Interface firmware details.
 */
public class LFXInterfaceFirmware {
    private final Date build;
    private final BigInteger install;
    private final int majorVersion;
    private final int minorVersion;

    public LFXInterfaceFirmware(Date build, BigInteger install, int majorVersion, int minorVersion) {
        this.build = build;
        this.install = install;
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }

    public Date getBuild() {
        return (Date)build.clone();
    }

    public BigInteger getInstall() {
        return install;
    }

    public int getMajorVersion() {
        return majorVersion;
    }
    
    public int getMinorVersion() {
        return minorVersion;
    }

    public String getVersion() {
        return String.format("%s.%s", getMajorVersion(), getMinorVersion());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.build);
        hash = 37 * hash + Objects.hashCode(this.install);
        hash = 37 * hash + this.majorVersion;
        hash = 37 * hash + this.minorVersion;
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
        final LFXInterfaceFirmware other = (LFXInterfaceFirmware) obj;
        if (!Objects.equals(this.build, other.build)) {
            return false;
        }
        if (!Objects.equals(this.install, other.install)) {
            return false;
        }
        if (this.majorVersion != other.majorVersion) {
            return false;
        }
        if (this.minorVersion != other.minorVersion) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "LFXInterfaceFirmware{" + "build=" + build + ", install=" + install + ", majorVersion=" + majorVersion + ", minorVersion=" + minorVersion + '}';
    }
}
