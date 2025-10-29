package com.reve.network.bandwidth;

import com.reve.audio.configs.AudioConfigurations;
import com.reve.audio.util.log.AudioLogWriterThread;
import com.reve.audio.util.log.LogPacket;
import com.reve.network.bandwidth.statistics.Loss;
import com.reve.network.bandwidth.statistics.RTT;
import com.reve.network.config.NetworkConfigurations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

public class LossBasedBandwidthEstimationThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(LossBasedBandwidthEstimationThread.class);

    private LinkedBlockingQueue<Loss> lossStatisticsQueue = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<RTT> rttStatisticsQueue = new LinkedBlockingQueue<>();

    private boolean running;
    private double acknowledgedLossRatio;

    private int averageRtt;
    private int currentBitRate;
    private int minimumBitRate;
    private int maximumBitRate;
    private int bitRateAdjustmentFactor;

    private static int counter;
    private AudioLogWriterThread audioLogWriterThread;

    public LossBasedBandwidthEstimationThread() {
        running = false;

        this.acknowledgedLossRatio = -1;
        this.averageRtt = Integer.MAX_VALUE;

        this.currentBitRate = AudioConfigurations.getInstance().getBitRate();
        this.maximumBitRate = AudioConfigurations.getInstance().getMaximumBitRate();
        this.minimumBitRate = AudioConfigurations.getInstance().getMinimumBitRate();
        this.bitRateAdjustmentFactor = AudioConfigurations.getInstance().getBitRateAdjustmentFactor();
        counter = 0;

        this.audioLogWriterThread = new AudioLogWriterThread();
    }

    @Override
    public void run() {

        LogPacket logFrame;
        running = true;
        int delay = NetworkConfigurations.getInstance().getBitRateUpdateIntervalMs();

        if (AudioConfigurations.getInstance().getIsAudioDebug()) {
            logger.info("Started LossBasedBandwidthEstimationThread");
        }

        this.audioLogWriterThread.start();

        while (running) {

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            calculateAverageAcknowledgedLossRatio();

            if (this.acknowledgedLossRatio >= 0) {
                adjustBitRate();

                if (!AudioConfigurations.getInstance().getIsAudioDebug()) {
                    logger.info("AdjustedBitrate={} bps\n", currentBitRate);
                }
            }

            logFrame = LogPacket.getNewFrame();
            logFrame.sequenceNumber = getNextSequenceNumber();
            logFrame.lossPercentage = (int) (this.acknowledgedLossRatio * 100.0);
            logFrame.bitRate = this.currentBitRate;
            pushLogFrameToBeSent(logFrame);

            resetParameters();
        }
    }

    public void pushToLossStatisticsQueue(Loss lossRtcpFrame) {
        this.lossStatisticsQueue.offer(lossRtcpFrame);
    }

    public void pushToRttStatisticsQueue(RTT rttRtcpFrame) {
        this.rttStatisticsQueue.offer(rttRtcpFrame);
    }

    private void calculateAverageAcknowledgedLossRatio() {
        try {
            int lossStatisticQueueLen = lossStatisticsQueue.size();
            if (lossStatisticQueueLen == 0) return;

            int totalPacketReceived = 0;
            int totalLostPacket = 0;

            Loss lossFrame;
            for (int i = 0; i < lossStatisticQueueLen; i++) {
                lossFrame = lossStatisticsQueue.take();
                totalLostPacket += lossFrame.lossCount;
                totalPacketReceived += lossFrame.totalReceivedPacket;
            }

            if (totalPacketReceived > 0) {
                this.acknowledgedLossRatio = (totalLostPacket / (totalPacketReceived * 1.0));
            }

            if (!AudioConfigurations.getInstance().getIsAudioDebug()) {
                logger.info("lossStatisticsQueueLen={} totalLost={} totalReceived={} AverageLossRatio={}", lossStatisticQueueLen, totalLostPacket, totalPacketReceived, this.acknowledgedLossRatio);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void calculateAverageRtt() {
        try {
            int rttStatisticsQueueLen = this.rttStatisticsQueue.size();
            if (rttStatisticsQueueLen == 0) return;

            this.averageRtt = 0;
            RTT frame;
            for (int i = 0; i < rttStatisticsQueueLen; i++) {
                frame = rttStatisticsQueue.take();
                this.averageRtt += frame.rtt;
            }
            this.averageRtt = this.averageRtt / rttStatisticsQueueLen;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void adjustBitRate() {

        int lossPercentage = (int) (acknowledgedLossRatio * 100);
        if (lossPercentage <= 0) {
            // Increase bitrate
            int newBitRate = (int) (((currentBitRate / (bitRateAdjustmentFactor * 1.0)) + 1) * bitRateAdjustmentFactor);
            if (newBitRate > maximumBitRate) {
                newBitRate = maximumBitRate;
            }

            currentBitRate = newBitRate;
            AudioConfigurations.getInstance().setBitRate(currentBitRate);
        } else if (lossPercentage >= 1 && lossPercentage <= 4) {

        } else {
            // Decrease bitrate
            int newBitRate = (int) (((1 - 48 * Math.pow((acknowledgedLossRatio - 0.04), 2)) * currentBitRate));
            if (!AudioConfigurations.getInstance().getIsAudioDebug()) {
                logger.info("newBitRate={}", newBitRate);
            }

            currentBitRate = Math.max(minimumBitRate, newBitRate);
            AudioConfigurations.getInstance().setBitRate(currentBitRate);
        }
    }

    private void resetParameters() {
        this.acknowledgedLossRatio = -1;
        this.averageRtt = Integer.MAX_VALUE;
    }

    private void pushLogFrameToBeSent(LogPacket frame) {
        this.audioLogWriterThread.pushToLogFrameQueue(frame);
    }

    private static int getNextSequenceNumber() {
        if (counter >= 32768) {
            counter = 0;
        }
        return ++counter;
    }

    public void shutDown() {
        running = false;
    }
}

