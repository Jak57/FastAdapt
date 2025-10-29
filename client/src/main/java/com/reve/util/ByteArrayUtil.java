package com.reve.util;

public class ByteArrayUtil {
    public static final int LENGTH_OF_INTEGER = 4;
    public static final int LENGTH_OF_LONG = 8;
    public static final char[] hexArray = "0123456789abcdef".toCharArray();

    public ByteArrayUtil() {
    }

    public static String byteArrayToHexString(byte[] bytes) {
        return byteArrayToHexString(bytes, 0, bytes.length);
    }

    public static String byteArrayToHexString(byte[] bytes, int offset, int len) {
        char[] hexChars = new char[len * 2];

        for(int j = 0; j < len; ++j) {
            int v = bytes[j + offset] & 255;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 15];
        }

        return new String(hexChars);
    }

    public static byte[] hexStringToByteArray(String s) {
        byte[] data = new byte[s.length() / 2];
        hexStringToByteArray(s, data, 0);
        return data;
    }

    public static int hexStringToByteArray(String s, byte[] destByteArray, int offset) {
        int len = s.length();
        if (len % 2 != 0) {
            return 0;
        } else {
            for(int i = 0; i < len; i += 2) {
                destByteArray[offset + i / 2] = (byte)((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
            }

            return len / 2;
        }
    }

    public static int getRandomData(byte[] array) {
        return getRandomData(array, 0, array.length);
    }

    public static int getRandomData(byte[] array, int offset, int len) {
        for(int i = 0; i < len; ++i) {
            array[offset + i] = (byte)Functions.random.nextInt(256);
        }

        return offset + len;
    }

    public static int getRandomHexData(byte[] data) {
        return getRandomHexData(data, 0, data.length);
    }

    public static int getRandomHexData(byte[] data, int index, int length) {
        for(int i = 0; i < length; ++i) {
            data[index++] = (byte)hexArray[Functions.random.nextInt(hexArray.length)];
        }

        return length;
    }

    public static int copyAsHexString(byte[] source, int sIndex, int sLength, byte[] dest, int dIndex) {
        int index = dIndex;

        for(int i = 0; i < sLength; ++i) {
            dest[index++] = (byte)hexArray[source[sIndex + i] >>> 4 & 15];
            dest[index++] = (byte)hexArray[source[sIndex + i] & 15];
        }

        return index - dIndex;
    }

    public static int putValue(byte[] data, int index, long value, int numberOfBytes) {
        for(int i = 1; i <= numberOfBytes; ++i) {
            data[index++] = (byte)((int)(value >> (numberOfBytes - i) * 8));
        }

        return index;
    }

    public static int getIntValue(byte[] data, int index, int numberOfBytes) {
        int result = 0;

        for(int i = 0; i < numberOfBytes; ++i) {
            result |= (data[index + i] & 255) << 8 * (numberOfBytes - i - 1);
        }

        return result;
    }

    public static long getLongValue(byte[] data, int index, int numberOfBytes) {
        long result = 0L;

        for(int i = 0; i < numberOfBytes; ++i) {
            result |= (long)(data[index + i] & 255) << 8 * (numberOfBytes - i - 1);
        }

        return result;
    }

    public static double getDoubleValue(byte[] data, int index, int numberOfBytes) {
        long result = getLongValue(data, index, numberOfBytes);
        return Double.longBitsToDouble(result);
    }

    public static String getString(byte[] data, int index, int length) {
        String str = new String(data, index, length);
        return str.equals("null") ? null : str;
    }

    public static boolean arraysEqual(byte[] a, int aOffset, int aLength, byte[] b, int bOffset, int bLength) {
        if (aLength != bLength) {
            return false;
        } else {
            for(int i = 0; i < aLength; ++i) {
                if (a[aOffset + i] != b[bOffset + i]) {
                    return false;
                }
            }

            return true;
        }
    }

    public static boolean arraysEqual(byte[] a, int aOffset, int aLength, String b) {
        if (aLength != b.length()) {
            return false;
        } else {
            for(int i = 0; i < aLength; ++i) {
                if (a[aOffset + i] != b.charAt(i)) {
                    return false;
                }
            }

            return true;
        }
    }
}