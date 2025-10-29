package com.reve.network.udp.rtcp;

import java.util.concurrent.LinkedBlockingQueue;

public class RtcpPacket {

    private static final LinkedBlockingQueue<RtcpPacket> freeFrame = new LinkedBlockingQueue<>();
    public byte[] data;
    public int len;

    public int feedbackPacketSequenceNo;
    public int packetStatusCount;
    public int baseSequenceNumber;
    public int lossPacketCount;

    public RtcpPacket() {
        this.data = new byte[2048];
        this.len = -1;
        this.feedbackPacketSequenceNo = -1;
        this.packetStatusCount = 0;
        this.baseSequenceNumber = -1;
        this.lossPacketCount = -1;
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

        frame.len = -1;
        frame.feedbackPacketSequenceNo = -1;
        frame.packetStatusCount = 0;
        frame.baseSequenceNumber = -1;
        frame.lossPacketCount = -1;

        freeFrame.add(frame);
    }
}
