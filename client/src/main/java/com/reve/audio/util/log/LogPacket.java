package com.reve.audio.util.log;

import java.util.concurrent.LinkedBlockingQueue;

public class LogPacket {

    private static final LinkedBlockingQueue<LogPacket> freeFrame = new LinkedBlockingQueue<>();
    public int sequenceNumber;
    public int lossPercentage;
    public int bitRate;

    LogPacket() {
        this.sequenceNumber = -1;
        this.lossPercentage = -1;
        this.bitRate = -1;
    }

    public static synchronized LogPacket getNewFrame() {
        LogPacket frame = freeFrame.poll();
        if (frame == null) {
            frame = new LogPacket();
        }
        return frame;
    }

    public static synchronized void freeFrame(LogPacket frame) {
        if (frame == null || freeFrame.size() >= 100) {
            return;
        }

        frame.sequenceNumber = -1;
        frame.lossPercentage = -1;
        frame.bitRate = -1;

        freeFrame.add(frame);
    }
}
