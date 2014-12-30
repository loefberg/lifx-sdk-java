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
import com.github.besherman.lifx.impl.entities.LFXPowerState;
import com.github.besherman.lifx.impl.entities.internal.structle.LxProtocolLight;
import com.github.besherman.lifx.impl.entities.internal.structle.StructleTypes.UInt16;

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
}
