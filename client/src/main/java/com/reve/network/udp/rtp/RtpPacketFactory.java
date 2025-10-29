package com.reve.network.udp.rtp;

import com.reve.audio.AudioFrameBytePacket;
import com.reve.util.ByteArrayUtil;

public class RtpPacketFactory {

    public static int buildAudioPacket(byte[] data, AudioFrameBytePacket frame) {
        int index = 0;
        // Frame data
        System.arraycopy(frame.data, 0, data, index, frame.len);
        index += frame.len;
        // 2 byte sequence number
        index = ByteArrayUtil.putValue(data, index, frame.sequenceNumber, 2);

        return index;
    }
}
