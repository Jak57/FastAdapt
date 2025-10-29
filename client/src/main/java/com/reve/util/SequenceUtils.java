package com.reve.util;

public class SequenceUtils {
    public static final int MAX_VALID_SEQUENCE = 65000;
    public static final int MIN_VALID_SEQUENCE = 1;

    public SequenceUtils() {
    }

    private static void checkValidInputSequence(int... args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("no input");
        } else {
            int[] var1 = args;
            int var2 = args.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                int x = var1[var3];
                if (isSequenceInvalid(x)) {
                    throw new IllegalArgumentException("invalid sequence number: " + x + " expected [" + 1 + "-" + '\ufde8' + "]");
                }
            }

        }
    }

    public static boolean isSequenceInvalid(int sequenceNumber) {
        return sequenceNumber < 1 || sequenceNumber > 65000;
    }

    /** @deprecated */
    @Deprecated
    public static int distance2(int source, int destination) {
        checkValidInputSequence(source, destination);
        return destination - source == 0 ? 0 : (destination - source > 0 ? (Math.min(destination - source, source + '\ufde8' - destination) == destination - source ? destination - source : -(source + '\ufde8' - destination)) : (Math.min(source - destination, destination + '\ufde8' - source) == source - destination ? -(source - destination) : destination + '\ufde8' - source));
    }

    public static int distance(int source, int destination) {
        checkValidInputSequence(source, destination);
        int diff = destination - source;
        if (Math.abs(diff) >= 32500) {
            if (diff < 0) {
                diff += 65000;
            } else {
                diff -= 65000;
            }
        }

        return diff;
    }

    public static int latestSequence(int a, int b) {
        return distance(a, b) > 0 ? b : a;
    }

    public static int previousSequence(int currentSequence) {
        checkValidInputSequence(currentSequence);
        return currentSequence <= 1 ? '\ufde8' : currentSequence - 1;
    }

    public static int nextSequence(int currentSequence) {
        checkValidInputSequence(currentSequence);
        return currentSequence >= 65000 ? 1 : currentSequence + 1;
    }

    public static int compareMin(int a, int b) {
        return -Integer.compare(distance(a, b), 0);
    }

    public static int compareMax(int a, int b) {
        return Integer.compare(distance(a, b), 0);
    }
}
