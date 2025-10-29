package com.reve.network.udp.rtp;

import com.reve.audio.AudioFrameBytePacket;
import com.reve.audio.AudioProcessor;
import com.reve.audio.configs.AudioConfigurations;
import com.reve.util.ByteArrayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;

public class RtpPacketFactory {

    private static final Logger logger = LoggerFactory.getLogger(RtpPacketFactory.class);

    public RtpPacketFactory() {

    }

    public static void parseAudioPacket(DatagramPacket packet) {
        AudioFrameBytePacket audioFrame = AudioFrameBytePacket.getNewFrame(0);

        int index = 0;

        if (AudioConfigurations.getInstance().getIsAudioDebug()) {
            logger.info("Length of audio frame is={}", packet.getLength());
        }

        if (packet.getLength() <= 2) {
            return; // Keep alive packet
        }

        int audioDataLength = packet.getLength() - 2;
        System.arraycopy(packet.getData(), index, audioFrame.data, index, audioDataLength);
        index += audioDataLength;

        int sequenceNumber = ByteArrayUtil.getIntValue(packet.getData(), index, 2);
        audioFrame.len = audioDataLength;
        audioFrame.sequenceNumber = sequenceNumber;

        if (AudioConfigurations.getInstance().getIsAudioDebug()) {
            logger.info("Parsing of audio frame with sequence number={} and length={} is completed", sequenceNumber, audioDataLength);
        }

        AudioProcessor.getInstance().pushToParsedAudioFramesQueue(audioFrame);
        RtpRouterThread.freeRTPDatagramPackets(packet);
    }
}
