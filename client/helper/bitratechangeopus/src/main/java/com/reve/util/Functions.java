package com.reve.util;

//ackage com.reve.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.UUID;

public class Functions {
    private static final char[] hexArray = "0123456789abcdef".toCharArray();
    public static final Random random = new Random();

    public Functions() {
    }

    public static String getCallerLocation() {
        StackTraceElement stackTraceElements = Thread.currentThread().getStackTrace()[3];
        return stackTraceElements.getClassName() + "[" + stackTraceElements.getLineNumber() + "]";
    }

    public static String getCurrentLocation() {
        StackTraceElement stackTraceElements = Thread.currentThread().getStackTrace()[2];
        return stackTraceElements.getClassName() + "[" + stackTraceElements.getLineNumber() + "]";
    }

    public static String getStackTrace() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StringBuilder result = new StringBuilder("\n");

        for(int i = 3; i < stackTrace.length; ++i) {
            StackTraceElement element = stackTrace[i];
            result.append("\t").append(element.getClassName()).append("[").append(element.getLineNumber()).append("]\n");
        }

        return result.toString();
    }

    public static synchronized long getVBNextSequenceID() {
        return getUniqueLong();
    }

    public static synchronized long getUniqueLong() {
        return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }

    public static int readByte(InputStream is, byte[] data, int offset, int length) throws IOException {
        int totalRead;
        int currentRead;
        for(totalRead = 0; totalRead < length; totalRead += currentRead) {
            currentRead = is.read(data, offset + totalRead, length - totalRead);
            if (currentRead < 0) {
                throw new IOException("Socket Closed:");
            }
        }

        return totalRead;
    }

    public static int readLine(InputStream is, byte[] data, int offset) throws IOException {
        int totalRead = 0;

        do {
            int currentRead = is.read(data, offset + totalRead, 1);
            if (currentRead < 0) {
                throw new IOException("Socket Closed:");
            }

            totalRead += currentRead;
        } while(data[totalRead - 1] != 10 || totalRead <= 1 || data[totalRead - 2] != 13);

        return totalRead;
    }

    public static String getRandomNumericString(int len) {
        StringBuilder builder = new StringBuilder(len);

        for(int i = 0; i < len; ++i) {
            builder.append(random.nextInt(10));
        }

        return builder.toString();
    }
}
