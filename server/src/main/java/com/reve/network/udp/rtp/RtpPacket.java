package com.reve.network.udp.rtp;

import com.reve.audio.AudioFrameBytePacket;

public abstract class RtpPacket implements Comparable<RtpPacket> {

    public int sequenceNumber;
    public int len;
    public byte[] data;

    public static void free(RtpPacket rtpPacket) {
        if (rtpPacket instanceof AudioFrameBytePacket) {
            AudioFrameBytePacket.freeFrame((AudioFrameBytePacket) rtpPacket);
        }
    }

    public abstract void onDestroy();
}
