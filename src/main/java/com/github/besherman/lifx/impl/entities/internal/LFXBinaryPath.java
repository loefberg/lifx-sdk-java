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

import java.util.Objects;

/**
 * The path to a bulb. This consists of two parts:
 * <ul>
 * <li>siteID:   The PAN controller that the message will pass through.</li>
 * <li>targetID: The bulb/bulbs that should get the message. This can be a 
 *               broadcast to all bulbs, all bulbs with a tag/tags, or one specific bulb.</li>
 * </ul>
 */
public class LFXBinaryPath {
    private final LFXSiteID siteID;
    private final LFXBinaryTargetID targetID;

    public LFXBinaryPath(LFXSiteID siteID, LFXBinaryTargetID targetID) {
        this.siteID = siteID;
        this.targetID = targetID;
    }
    
    public LFXBinaryPath(LFXSiteID siteID) {
        this(siteID, new LFXBinaryTargetID());
    }    


    public LFXSiteID getSiteID() {
        return siteID;
    }

    public LFXBinaryTargetID getBinaryTargetID() {
        return targetID;
    }
    
    @Override
    public String toString() {
        return "LFXBinaryPath{" + "siteID=" + siteID + ", targetID=" + targetID + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.siteID);
        hash = 29 * hash + Objects.hashCode(this.targetID);
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
        final LFXBinaryPath other = (LFXBinaryPath) obj;
        if (!Objects.equals(this.siteID, other.siteID)) {
            return false;
        }
        if (!Objects.equals(this.targetID, other.targetID)) {
            return false;
        }
        return true;
    }
}
