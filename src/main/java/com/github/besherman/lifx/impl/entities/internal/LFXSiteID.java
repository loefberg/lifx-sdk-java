/*
 * The MIT License
 *
 * Created by Jarrod Boyes on 24/03/14.
 * Copyright (c) 2014 LIFX Labs. All rights reserved.
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
package com.github.besherman.lifx.impl.entities.internal;

import java.util.Arrays;

/**
 * What is a site you ask? Good question.
 * It is always specified in the packet format. And it has the same length
 * as a MAC address. lifxjs says: "MAC address of gateway PAN controller bulb"
 * But it is not the address of the interface that sends us messages. 
 * Current working hypothesis: It's the PAN controller's mesh interface MAC address.
 * 
 * This is part of {@link LFXBinaryPath}.
 */
public class LFXSiteID {
    private static final int LFX_SITE_ID_NUMBER_OF_BYTES = 6;

    private final byte[] data;

    // When a device hasn't been added to a site yet, it will have a 'zero' Site ID.
    public LFXSiteID() {
        this.data = new byte[LFX_SITE_ID_NUMBER_OF_BYTES];
        Arrays.fill(data, (byte)0);
    }
    
    public LFXSiteID(byte[] data) {
        this.data = Arrays.copyOf(data, data.length);        
    }
    
    public LFXSiteID(String siteIDString) {
        this(LFXByteUtils.hexStringToByteArray(siteIDString));
    }


    public String getStringValue() {
        if (this.isZeroSite()) {
            return "*";
        }

        return LFXByteUtils.byteArrayToHexString(data);
    }

    public byte[] getDataValue() {
        return data;
    }

    
    public boolean isZeroSite() {
        return LFXByteUtils.isByteArrayEmpty(data);
    }

    @Override
    public String toString() {
        return "LFXSiteID{" + getStringValue() + "}";
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Arrays.hashCode(this.data);
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
        final LFXSiteID other = (LFXSiteID) obj;
        if (!Arrays.equals(this.data, other.data)) {
            return false;
        }
        return true;
    }

    
    
}
