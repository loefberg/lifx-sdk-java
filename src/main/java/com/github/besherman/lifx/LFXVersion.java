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
 * Information about a specific product.
 */
public class LFXVersion {
    private final long product;
    private final long vendor;
    private final long version;

    public LFXVersion(long product, long vendor, long version) {
        this.product = product;
        this.vendor = vendor;
        this.version = version;
    }

    public long getProduct() {
        return product;
    }

    public long getVendor() {
        return vendor;
    }

    public long getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (int) (this.product ^ (this.product >>> 32));
        hash = 37 * hash + (int) (this.vendor ^ (this.vendor >>> 32));
        hash = 37 * hash + (int) (this.version ^ (this.version >>> 32));
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
        final LFXVersion other = (LFXVersion) obj;
        if (this.product != other.product) {
            return false;
        }
        if (this.vendor != other.vendor) {
            return false;
        }
        if (this.version != other.version) {
            return false;
        }
        return true;
    }  

    @Override
    public String toString() {
        return "Version{" + "product=" + product + ", vendor=" + vendor + ", version=" + version + '}';
    }
}
