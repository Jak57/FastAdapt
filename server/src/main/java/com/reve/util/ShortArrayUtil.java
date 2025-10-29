package com.reve.util;

public class ShortArrayUtil {
    public ShortArrayUtil() {
    }

    public static int convertByteToShort(byte[] input, int offset, int inputLen, short[] output, int outOffset) {
        int outIndex = outOffset;

        for(int i = 0; i < inputLen; i += 2) {
            output[outIndex] = (short)(input[offset + i + 1] & 255);
            output[outIndex] = (short)(output[outIndex] << 8 | input[offset + i] & 255);
            ++outIndex;
        }

        return outIndex - outOffset;
    }

    public static int convertShortToByte(short[] input, int offset, int inputLen, byte[] output, int outOffset) {
        int outIndex = outOffset;

        for(int i = 0; i < inputLen; ++i) {
            output[outIndex++] = (byte)(input[offset + i] & 255);
            output[outIndex++] = (byte)(input[offset + i] >> 8 & 255);
        }

        return outIndex - outOffset;
    }
}
