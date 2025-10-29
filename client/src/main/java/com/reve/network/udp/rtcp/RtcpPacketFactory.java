package com.reve.network.udp.rtcp;

import com.reve.audio.configs.AudioConfigurations;
import com.reve.network.bandwidth.statistics.Loss;
import com.reve.network.bandwidth.statistics.RTT;
import com.reve.network.util.MediaManager;
import com.reve.util.ByteArrayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;

public class RtcpPacketFactory {

    private static final Logger logger = LoggerFactory.getLogger(RtcpPacketFactory.class);

    public RtcpPacketFactory() {

    }

    public static void parseRtcpPacket(DatagramPacket packet) {
        if (packet.getLength() <= 2) return; // Ignore Keep Alive Packet

        int index = 0;

        int ignore = ByteArrayUtil.getIntValue(packet.getData(), index, 3);
        index += 3;

        // packet type
        int packetType = ByteArrayUtil.getIntValue(packet.getData(), index, 1);
        index += 1;

        switch(packetType) {
            case RtcpPacketType.PACKET_LOSS_STATISTICS_PACKET:
                parseRtcpPacketLossStatisticsPacket(packet, index);
                break;

            case RtcpPacketType.RTT_PACKET:
                parseRtcpPacketRtt(packet, index);
                break;
        }
    }

    public static void parseRtcpPacketLossStatisticsPacket(DatagramPacket packet, int index) {
//        RtcpPacket rtcpFrame = RtcpPacket.getNewFrame();

        // feedback packet sequence number
        int feedbackPacketSequenceNo = ByteArrayUtil.getIntValue(packet.getData(), index, 2);
        index += 2;

        // packet status count
        int packetStatusCount = ByteArrayUtil.getIntValue(packet.getData(), index, 1);
        index += 1;

        // base sequence number
        int baseSequenceNumber = ByteArrayUtil.getIntValue(packet.getData(), index, 2);
        index += 2;

        // lost packet count
        int lostPacketCount = ByteArrayUtil.getIntValue(packet.getData(), index, 1);
        index += 1;

        if (!AudioConfigurations.getInstance().getIsAudioDebug() && packetStatusCount > 0) {
            logger.info("Loss statistics: feedbackPacketSequenceNo={} packetStatusCount={} baseSequenceNumber={} lostPacketCount={} length={}", feedbackPacketSequenceNo, packetStatusCount, baseSequenceNumber, lostPacketCount, packet.getLength());
        }

        if (packetStatusCount > 0) {
            Loss frame = Loss.getNewFrame();
            frame.lossCount = lostPacketCount;
            frame.totalReceivedPacket = packetStatusCount;
            pushLossStatisticsFrameToBeSent(frame);
        }

    }

    private static void pushLossStatisticsFrameToBeSent(Loss frame) {
        MediaManager.getInstance().getLossBasedBandwidthEstimationThread().pushToLossStatisticsQueue(frame);
    }

    public static void parseRtcpPacketRtt(DatagramPacket packet, int index) {

        // sequence number
        int sequenceNumber = ByteArrayUtil.getIntValue(packet.getData(), index, 2);
        index += 2;

        // send time stamp
        long sendTimestamp = ByteArrayUtil.getLongValue(packet.getData(), index, 8);
        index += 8;

        long rtt = System.currentTimeMillis() - sendTimestamp;
        if (AudioConfigurations.getInstance().getIsAudioDebug()) {
            logger.info("PacketType=1 sequenceNo={} sendTimestamp={} ms rtt={} ms\n\n", sequenceNumber, sendTimestamp, rtt);
        }

        RTT frame = RTT.getNewFrame();
        frame.rtt = (int) rtt;
        frame.timestamp = System.currentTimeMillis();
        pushRttFrameToBeSent(frame);
    }

    private static void pushRttFrameToBeSent(RTT frame) {
        MediaManager.getInstance().getLossBasedBandwidthEstimationThread().pushToRttStatisticsQueue(frame);
    }

    public static int getReceivedPacketCount(byte[] packetStatus) {
        int count = 0;
        for (int i = 0; i < packetStatus.length; i++) {
            count += Integer.bitCount(packetStatus[i] & 0XFF);
        }
        return count;
    }

    public static void printPacketStatus(byte[] bitmap) {
        for (int i = 0; i < bitmap.length; i++) {
            System.out.printf("byte %d: %8s\n", i, String.format("%8s", Integer.toBinaryString(bitmap[i] & 0xFF)).replace(' ', '0'));
            int count = Integer.bitCount(bitmap[i] & 0xFF);
            System.out.println("value " + bitmap[i] + " count: " + count);
        }
    }

    public static int buildRttPacket(RtcpPacket frame) {
        int index = 0;

        // 4 byte for packet type (First 3 byte is set to 0 to represent RTCP packet, Last 1 byte is used to differentiate between Loss/RTT packet: 1 represents Loss Packet RTT Packet)
        index = ByteArrayUtil.putValue(frame.data, index, 0, 3);
        index = ByteArrayUtil.putValue(frame.data, index, 1, 1);

        // Sequence Number
        index = ByteArrayUtil.putValue(frame.data, index, frame.baseSequenceNumber, 2);

        // Send timestamp
        long time = System.currentTimeMillis();
        index = ByteArrayUtil.putValue(frame.data, index, time, 8);

        return index;
    }
}
