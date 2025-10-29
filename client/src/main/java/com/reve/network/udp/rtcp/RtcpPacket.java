package com.reve.network.udp.rtcp;

import com.reve.audio.configs.AudioConfigurations;

import java.util.concurrent.LinkedBlockingQueue;

public class RtcpPacket {

    private static final LinkedBlockingQueue<RtcpPacket> freeFrame = new LinkedBlockingQueue<>();
    public byte[] data;
    public byte[] packetStatus;
    public int len;
    public int baseSequenceNumber;
    public int feedbackPacketCountNumber;
    public int packetStatusCount;
    public long referenceTime;

    public RtcpPacket() {
        this.data = new byte[2048];
        this.packetStatus = new byte[AudioConfigurations.getInstance().getPacketStatusByteArraySize()];
        this.len = -1;
        this.baseSequenceNumber = -1;
        this.feedbackPacketCountNumber = -1;
        this.referenceTime = -1;
        this.packetStatusCount = 0;
    }

    public static synchronized  RtcpPacket getNewFrame() {
        RtcpPacket frame = freeFrame.poll();
        if (frame == null) {
            frame = new RtcpPacket();
        }
        return frame;
    }

    public static synchronized void freeFrame(RtcpPacket frame) {
        if (frame == null || freeFrame.size() >= 100) {
            return;
        }

        for (int i = 0; i < frame.packetStatus.length; i++) {
            frame.packetStatus[i] = 0;
        }

        frame.len = -1;
        frame.baseSequenceNumber = -1;
        frame.feedbackPacketCountNumber = -1;
        frame.referenceTime = -1;
        frame.packetStatusCount = 0;

        freeFrame.add(frame);
    }
}
