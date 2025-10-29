package com.reve.network.udp.rtcp;

import com.reve.audio.AudioFrameBytePacket;
import com.reve.audio.AudioManager;
import com.reve.audio.configs.AudioConfigurations;
import com.reve.util.SequenceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

public class RtcpBuilderThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(RtcpBuilderThread.class);
    private final LinkedBlockingQueue<AudioFrameBytePacket> frameForGeneratingRtcpQueue;

    private boolean running;
    private boolean firstFrame;
    private int feedbackPacketCount;

    public RtcpBuilderThread() {
        this.running = false;
        this.firstFrame = true;
        this.feedbackPacketCount = SequenceUtils.MIN_VALID_SEQUENCE;
        this.frameForGeneratingRtcpQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void run() {
        running = true;
        if (AudioConfigurations.getInstance().getIsAudioDebug()) {
            logger.info("Started RtcpBuilderThread");
        }

        int delay = AudioConfigurations.getInstance().getRtcpPacketSendingIntervalMs();

        while (running) {
            long t1 = System.currentTimeMillis();
            RtcpPacket frame = getRtcpPacketToBeSent();

            if (frame != null) {
                if (AudioConfigurations.getInstance().getIsAudioDebug()) {
                    logger.info("Info of Rtcp frame: len={} packet status_count={} frame_number={}", frame.len, frame.packetStatusCount, frame.feedbackPacketSequenceNo);
                }
                pushToRtcpFramesForSendingToClient(frame);
            }

            long t2 = System.currentTimeMillis();
            long diff = t2 - t1;
            try {
                Thread.sleep(Math.max((delay - diff), 0));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void pushToFrameForGeneratingRtcpQueue(AudioFrameBytePacket frame) {
        frameForGeneratingRtcpQueue.offer(frame);
    }

    private RtcpPacket getRtcpPacketToBeSent() {

        RtcpPacket frame = RtcpPacket.getNewFrame();
        frame.feedbackPacketSequenceNo = getNextFeedbackPacketSequenceNo();
        frame.packetStatusCount = frameForGeneratingRtcpQueue.size();;

        if (frame.packetStatusCount == 0) {
            frame.len = RtcpPacketFactory.buildPacketLossRtcpFrame(frame);
            return frame;
        } else {
            AudioFrameBytePacket rtpFrame = null;
            try {
                rtpFrame = frameForGeneratingRtcpQueue.peek();
                frame.baseSequenceNumber = rtpFrame.sequenceNumber;

                int totalReceivedPacketWithinLimit = 0;
                for (int i = 0; i < frame.packetStatusCount; i++) {
                    rtpFrame = frameForGeneratingRtcpQueue.take();
                    int relativeDistance = SequenceUtils.distance(frame.baseSequenceNumber, rtpFrame.sequenceNumber);

                    if (relativeDistance >= 0 && relativeDistance < frame.packetStatusCount) {
                        totalReceivedPacketWithinLimit += 1;;
                    }
                }

                if (!AudioConfigurations.getInstance().getIsAudioDebug()) {
                    logger.info("feedbackPacketCount={} baseSequenceNo={} packetStatusCount={} totalReceivedWithinLimit={} lossCount={}", frame.feedbackPacketSequenceNo, frame.baseSequenceNumber, frame.packetStatusCount, totalReceivedPacketWithinLimit, frame.packetStatusCount - totalReceivedPacketWithinLimit);
                }

                frame.lossPacketCount = frame.packetStatusCount - totalReceivedPacketWithinLimit;
                frame.len = RtcpPacketFactory.buildPacketLossRtcpFrame(frame);
                return frame;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private int getNextFeedbackPacketSequenceNo() {
        if (firstFrame) {
            firstFrame = false;
            return SequenceUtils.MIN_VALID_SEQUENCE;
        }
        this.feedbackPacketCount = SequenceUtils.nextSequence(this.feedbackPacketCount);
        return this.feedbackPacketCount;
    }

    public void pushToRtcpFramesForSendingToClient(RtcpPacket frame) {
        AudioManager.getInstance().getRtcpSenderThread().pushRtcpFrameToBeSent(frame);
    }

    public void shutDown() {
        running = false;
        frameForGeneratingRtcpQueue.clear();
    }
}
