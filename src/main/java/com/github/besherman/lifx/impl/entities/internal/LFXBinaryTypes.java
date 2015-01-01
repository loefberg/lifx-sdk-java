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

import com.github.besherman.lifx.LFXHSBKColor;
import com.github.besherman.lifx.LFXInterfaceFirmware;
import com.github.besherman.lifx.impl.entities.LFXPowerState;
import com.github.besherman.lifx.impl.entities.internal.structle.LxProtocolLight;
import com.github.besherman.lifx.impl.entities.internal.structle.StructleTypes;
import com.github.besherman.lifx.impl.entities.internal.structle.StructleTypes.UInt16;
import com.github.besherman.lifx.impl.entities.internal.structle.StructleTypes.UInt32;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

public class LFXBinaryTypes {

    public static UInt16 getLFXProtocolPowerLevelFromLFXPowerState(LFXPowerState powerState) {
        switch (powerState) {
            case OFF:
                return new UInt16(0);
            case ON:
                return new UInt16(1);
        }

        return new UInt16(1);
    }

    public static LFXPowerState getLFXPowerStateFromLFXProtocolPowerLevel(UInt16 powerLevel) {
        if (powerLevel.getValue() == 0) {
            return LFXPowerState.OFF;
        } else {
            return LFXPowerState.ON;
        }
    }

    public static LFXHSBKColor getLFXHSBKColorFromLXProtocolLightHsbk(LxProtocolLight.Hsbk protocolHsbk) {
        float hue = (float) protocolHsbk.getHue().getValue() * 360.0f / (float) UInt16.MAX_U16_VALUE;
        float saturation = (float) protocolHsbk.getSaturation().getValue() / (float) UInt16.MAX_U16_VALUE;
        float brightness = (float) protocolHsbk.getBrightness().getValue() / (float) UInt16.MAX_U16_VALUE;
        int kelvin = protocolHsbk.getKelvin().getValue();

        LFXHSBKColor color = new LFXHSBKColor(hue, saturation, brightness, kelvin);

        return color;
    }

    public static LxProtocolLight.Hsbk getLXProtocolLightHsbkFromLFXHSBKColor(LFXHSBKColor color) {
        int hue = (int) (color.getHue() / 360.0 * (float) UInt16.MAX_U16_VALUE);
        int saturation = (int) (color.getSaturation() * (float) UInt16.MAX_U16_VALUE);
        int brightness = (int) (color.getBrightness() * (float) UInt16.MAX_U16_VALUE);
        int kelvin = color.getKelvin();

        UInt16 wrappedHue = new UInt16(hue);
        UInt16 wrappedSaturation = new UInt16(saturation);
        UInt16 wrappedBrightness = new UInt16(brightness);
        UInt16 wrappedKelvin = new UInt16(kelvin);

        Object padding = new Object();

        LxProtocolLight.Hsbk lightHSBK = new LxProtocolLight.Hsbk(padding, wrappedHue, wrappedSaturation, wrappedBrightness, wrappedKelvin);

        return lightHSBK;
    }
    

    
    public static LFXInterfaceFirmware createFirmware(StructleTypes.UInt64 build, StructleTypes.UInt64 install, UInt32 version) {
        int majorVersion = (int)((version.getValue() & 0xffff0000) >> 16); 
        int minorVersion = (int)(version.getValue() & 0xffff);
        Date buildDate = getBuildDate(build);
        return new LFXInterfaceFirmware(buildDate, install.getBigIntegerValue(), majorVersion, minorVersion);        
    }
    
    private static Date getBuildDate(StructleTypes.UInt64 build) {
        BigInteger bigBuild = build.getBigIntegerValue();
        if(bigBuild.compareTo(new BigInteger("1200000000000000000")) > 0) {
            return new Date(bigBuild.divide(new BigInteger("1000000")).longValue());        
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date(0));
            
            byte[] bytes = build.getBytes();
            cal.set(Calendar.YEAR, 2000 + bytes[7]);
            cal.set(Calendar.DAY_OF_MONTH, bytes[3]);
            cal.set(Calendar.HOUR, bytes[2]);
            cal.set(Calendar.MINUTE, bytes[1]);
            cal.set(Calendar.SECOND, bytes[0]);
            
            char[] mon = new char[3];
            mon[0] = (char)bytes[6];
            mon[1] = (char)bytes[5];
            mon[2] = (char)bytes[4];
            String month = new String(mon);
            
            switch(month) {
                case "Jan":
                    cal.set(Calendar.MONTH, 0);
                    break;
                case "Feb":
                    cal.set(Calendar.MONTH, 1);
                    break;
                case "Mar":
                    cal.set(Calendar.MONTH, 2);
                    break;
                case "Apr":
                    cal.set(Calendar.MONTH, 3);
                    break;
                case "May":
                    cal.set(Calendar.MONTH, 4);
                    break;
                case "Jun":
                    cal.set(Calendar.MONTH, 5);
                    break;
                case "Jul":
                    cal.set(Calendar.MONTH, 6);
                    break;
                case "Aug":
                    cal.set(Calendar.MONTH, 7);
                    break;
                case "Sep":
                    cal.set(Calendar.MONTH, 8);
                    break;
                case "Oct":
                    cal.set(Calendar.MONTH, 9);
                    break;
                case "Nov":
                    cal.set(Calendar.MONTH, 10);
                    break;
                case "Dec":
                    cal.set(Calendar.MONTH, 11);
                    break;
            }          
            return cal.getTime();
        }        
    }    
        
}
