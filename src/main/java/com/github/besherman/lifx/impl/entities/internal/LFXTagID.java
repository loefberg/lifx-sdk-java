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

package com.github.besherman.lifx.impl.entities.internal;

import com.github.besherman.lifx.impl.entities.internal.structle.StructleTypes.UInt64;
import java.math.BigInteger;
import java.util.EnumSet;
import java.util.Set;

/**
 *
 * @author Richard
 */
public enum LFXTagID {
    TAG_01(bits(0)), 
    TAG_02(bits(1)), 
    TAG_03(bits(2)), 
    TAG_04(bits(3)), 
    TAG_05(bits(4)), 
    TAG_06(bits(5)), 
    TAG_07(bits(6)), 
    TAG_08(bits(7)), 
    TAG_09(bits(8)), 
    TAG_10(bits(9)), 
    TAG_11(bits(10)), 
    TAG_12(bits(11)), 
    TAG_13(bits(12)), 
    TAG_14(bits(13)), 
    TAG_15(bits(14)), 
    TAG_16(bits(15)), 
    TAG_17(bits(16)), 
    TAG_18(bits(17)), 
    TAG_19(bits(18)), 
    TAG_20(bits(19)), 
    TAG_21(bits(20)), 
    TAG_22(bits(21)), 
    TAG_23(bits(22)), 
    TAG_24(bits(23)), 
    TAG_25(bits(24)), 
    TAG_26(bits(25)), 
    TAG_27(bits(26)), 
    TAG_28(bits(27)), 
    TAG_29(bits(28)), 
    TAG_30(bits(29)), 
    TAG_31(bits(30)), 
    TAG_32(bits(31)), 
    TAG_33(bits(32)), 
    TAG_34(bits(33)), 
    TAG_35(bits(34)), 
    TAG_36(bits(35)), 
    TAG_37(bits(36)), 
    TAG_38(bits(37)), 
    TAG_39(bits(38)), 
    TAG_40(bits(39)), 
    TAG_41(bits(40)), 
    TAG_42(bits(41)), 
    TAG_43(bits(42)), 
    TAG_44(bits(43)), 
    TAG_45(bits(44)), 
    TAG_46(bits(45)), 
    TAG_47(bits(46)), 
    TAG_48(bits(47)), 
    TAG_49(bits(48)), 
    TAG_50(bits(49)), 
    TAG_51(bits(50)), 
    TAG_52(bits(51)), 
    TAG_53(bits(52)), 
    TAG_54(bits(53)), 
    TAG_55(bits(54)), 
    TAG_56(bits(55)), 
    TAG_57(bits(56)), 
    TAG_58(bits(57)), 
    TAG_59(bits(58)), 
    TAG_60(bits(59)), 
    TAG_61(bits(60)), 
    TAG_62(bits(61)), 
    TAG_63(bits(62)), 
    TAG_64(bits(63));
        
    public static EnumSet<LFXTagID> unpack(UInt64 value) {
        EnumSet<LFXTagID> result = EnumSet.allOf(LFXTagID.class);
        BigInteger tmp = value.getBigIntegerValue();
        for(LFXTagID tag: values()) {
            if(tmp.and(tag.value).equals(BigInteger.ZERO)) {
                result.remove(tag);
            }
        }
        return result;
    }
    
    public static UInt64 pack(Set<LFXTagID> tags) {
        BigInteger value = BigInteger.ZERO;
        for(LFXTagID tag: tags) {
            value = value.or(tag.value);
        }
        
        return new UInt64(value);
    }    


    ////////////////////////////////////////////////////////////////////////////
    // Object 
    ////////////////////////////////////////////////////////////////////////////
    private final BigInteger value;
    
    private LFXTagID(BigInteger value) {
        this.value = value;
    }
    
    public String bitField() {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < 64; i++) {
            builder.append(value.testBit(i) ? "1" : "0");
        }
        return builder.toString();
    }
    
    
    private static BigInteger bits(int pow) {
        return (new BigInteger("2")).pow(pow);
    }
}
