package com.reve.network.udp.rtcp;

import com.reve.audio.AudioManager;
import com.reve.audio.configs.AudioConfigurations;
import com.reve.network.config.NetworkConfigurations;
import com.reve.util.SequenceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RtcpGenerationThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(RtcpGenerationThread.class);
    private boolean running;

    public RtcpGenerationThread() {
        running = false;
    }

    @Override
    public void run() {
        running = true;

        if (AudioConfigurations.getInstance().getIsAudioDebug()) {
            logger.info("Started RtcpGenerationThread");
        }

        int sequenceNumber = SequenceUtils.MIN_VALID_SEQUENCE;
        while (running) {

            RtcpPacket frame = RtcpPacket.getNewFrame();
            AudioManager.getInstance().prepareRttRtcpPacketToBeSent(frame, sequenceNumber);
            sequenceNumber = SequenceUtils.nextSequence(sequenceNumber);

            try {
                Thread.sleep(NetworkConfigurations.getInstance().getRtcpPacketSendIntervalMs());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void shutDown() {
        running = false;
    }
}
