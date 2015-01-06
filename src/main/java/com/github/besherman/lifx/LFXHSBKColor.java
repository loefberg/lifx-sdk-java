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
package com.github.besherman.lifx;

import java.awt.Color;

/**
 * Color described by hue, saturation, brightness and kelvin.
 * 
 * For saturated colors the kelvin temperature does not appear to matter.
 */
public class LFXHSBKColor {
    public static final int MIN_KELVIN = 0;
    public static final int MAX_KELVIN = 10000;

    private final float hue;				// 0.0 - 360.0
    private final float saturation;			// 0.0 - 1.0
    private final float brightness;			// 0.0 - 1.0
    private final int kelvin;				// 0 - 10,000

    /**
     * Creates a new color with a kelvin of 6500 (to match D6500).
     */
    public LFXHSBKColor(Color color) {
        this(color, 6500);
    }
    
    /**
     * Creates a new HSBK color from an RGB color and a kelvin value.
     * @param color an RGB color
     * @param kelvin the kelvin value in the range [0, 10000]
     */
    public LFXHSBKColor(Color color, int kelvin) {
        if(color == null) {
            throw new IllegalArgumentException("color can not be null");
        }
        if(kelvin < MIN_KELVIN || kelvin > MAX_KELVIN) {
            throw new IllegalArgumentException("kelvin must be between 0 and 10 000");
        }
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        this.hue = hsb[0] * 360;
        this.saturation = hsb[1];
        this.brightness = hsb[2];
        this.kelvin = kelvin;
    }
    
    /**
     * Creates a new HSBK color.
     * @param hue hue in the range [0, 360]
     * @param saturation saturation in the range [0, 1]
     * @param brightness brightness in the range [0, 1]
     * @param kelvin kelvin in the range [0, 10000]
     */
    public LFXHSBKColor(float hue, float saturation, float brightness, int kelvin) {
        if(hue < 0 || hue > 360) {
            throw new IllegalArgumentException("hue must be between 0 and 360");
        }
        if(saturation < 0 || saturation > 1) {
            throw new IllegalArgumentException("saturation must be between 0 and 1");
        }
        if(brightness < 0 || brightness > 1) {
            throw new IllegalArgumentException("brightness must be between 0 and 1");
        }
        if(kelvin < MIN_KELVIN || MAX_KELVIN > 10000) {
            throw new IllegalArgumentException("kelvin must be between 0 and 10 000");
        }
        
        this.hue = hue;
        this.saturation = saturation;
        this.brightness = brightness;
        this.kelvin = kelvin;
    }

    /**
     * Returns the hue component of the color.
     * @return [0, 360]
     */
    public float getHue() {
        return hue;
    }

    /**
     * Returns the saturation component of the color.
     * @return [0, 1]
     */
    public float getSaturation() {
        return saturation;
    }

    /**
     * Returns the brightness component of the color.
     * @return [0, 1]
     */
    public float getBrightness() {
        return brightness;
    }

    /**
     * Returns the kelvin component of the color.
     * @return [0, 10000]
     */
    public int getKelvin() {
        return kelvin;
    }

    @Override
    public String toString() {
        return "LFXHSBKColor{" + "hue=" + hue + ", sat=" + saturation + ", bri=" + brightness + ", kelvin=" + kelvin + '}';
    }

    /**
     * Returns the average color.
     */
    public static LFXHSBKColor averageOfColors(LFXHSBKColor[] colors) {
        if (colors.length == 0) {
            return null;
        }

        float hueXTotal = 0;
        float hueYTotal = 0;
        float saturationTotal = 0;
        float brightnessTotal = 0;
        long kelvinTotal = 0;

        for (LFXHSBKColor aColor : colors) {
            hueXTotal += Math.sin(aColor.hue * Math.PI / 180.0);
            hueYTotal += Math.cos(aColor.hue * Math.PI / 180.0);
            saturationTotal += aColor.saturation;
            brightnessTotal += aColor.brightness;

            if (aColor.kelvin == 0) {
                kelvinTotal += 3500;
            } else {
                kelvinTotal += aColor.kelvin;
            }
        }

        float M_1_PI = (float) (1.0f / Math.PI);

        float hue = (float) (Math.atan2(hueXTotal, hueYTotal) * 0.5 * M_1_PI);
        if (hue < 0.0) {
            hue += 1.0;
        }
        float saturation = saturationTotal / (float) colors.length;
        float brightness = brightnessTotal / (float) colors.length;
        int kelvin = (int) (kelvinTotal / colors.length);

        return new LFXHSBKColor(hue, saturation, brightness, kelvin);
    }   


    @Override
    public int hashCode() {
        int hash = 5;
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
        final LFXHSBKColor other = (LFXHSBKColor) obj;
        if (Float.floatToIntBits(this.hue) != Float.floatToIntBits(other.hue)) {
            return false;
        }
        if (Float.floatToIntBits(this.saturation) != Float.floatToIntBits(other.saturation)) {
            return false;
        }
        if (Float.floatToIntBits(this.brightness) != Float.floatToIntBits(other.brightness)) {
            return false;
        }
        if (this.kelvin != other.kelvin) {
            return false;
        }
        return true;
    }
}
