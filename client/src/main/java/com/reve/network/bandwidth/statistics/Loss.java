package com.reve.network.bandwidth.statistics;

import java.util.concurrent.LinkedBlockingQueue;

public class Loss {

    private static final LinkedBlockingQueue<Loss> freeFrame = new LinkedBlockingQueue<>();
    public int lossCount;
    public int totalReceivedPacket;
//    public int acknowledgedBitRate;
    public long timestamp;

    public Loss() {
        this.lossCount = -1;
        this.totalReceivedPacket = -1;
//        this.acknowledgedBitRate = -1;
        this.timestamp = -1;
    }

    public static synchronized Loss getNewFrame() {
        Loss frame = freeFrame.poll();
        if (frame == null) {
            frame = new Loss();
        }
        return frame;
    }

    public static synchronized void freeFrame(Loss frame) {
        if (frame == null || freeFrame.size() >= 100) {
            return;
        }

        frame.lossCount = -1;
        frame.totalReceivedPacket = -1;
//        frame.acknowledgedBitRate = -1;
        frame.timestamp = -1;

        freeFrame.add(frame);
    }
}
