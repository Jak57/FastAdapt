package com.reve.network.udp.rtcp;

import com.reve.util.ByteArrayUtil;

public class RtcpPacketFactory {

    public RtcpPacketFactory() {

    }

    public static synchronized int buildPacketLossRtcpFrame(RtcpPacket frame) {
        int index = 0;

        // packet type (First 3 byte is set to 0 to represent RTCP packet, Last 1 byte is used to differentiate between Loss/RTT packet type: 0 represents Loss Statistics Rtcp Packet)
        index = ByteArrayUtil.putValue(frame.data, index, 0, 3);
        index = ByteArrayUtil.putValue(frame.data, index, 0, 1);

        // feedback packet sequence number
        index = ByteArrayUtil.putValue(frame.data, index, frame.feedbackPacketSequenceNo, 2);

        // packet status count
        index = ByteArrayUtil.putValue(frame.data, index, frame.packetStatusCount, 1);

        // base sequence number
        index = ByteArrayUtil.putValue(frame.data, index, frame.baseSequenceNumber, 2);

        // lost packet count
        index = ByteArrayUtil.putValue(frame.data, index, frame.lossPacketCount, 1);

        return index;
    }
}
