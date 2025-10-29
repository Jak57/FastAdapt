package com.reve.network.bandwidth.statistics;

import java.util.concurrent.LinkedBlockingQueue;

public class RTT {

    private static final LinkedBlockingQueue<RTT> freeFrame = new LinkedBlockingQueue<>();
    public int rtt;
    public long timestamp;

    public RTT() {
        this.rtt = -1;
        this.timestamp = -1;
    }

    public static synchronized RTT getNewFrame() {
        RTT frame = freeFrame.poll();
        if (frame == null) {
            frame = new RTT();
        }
        return frame;
    }

    public static void freeFrame(RTT frame) {
        if (frame == null || freeFrame.size() >= 100) {
            return;
        }

        frame.rtt = -1;
        frame.timestamp = -1;
        freeFrame.add(frame);
    }
}
