package com.reve.network.util;

import com.reve.network.bandwidth.LossBasedBandwidthEstimationThread;
import com.reve.network.config.NetworkConfigurations;
import com.reve.network.udp.rtcp.RtcpGenerationThread;
import com.reve.network.udp.rtcp.RtcpSenderThread;
import com.reve.network.udp.rtp.RtpSenderThread;

public class MediaManager {
    public static MediaManager mediaManager = null;

    private RtpSenderThread rtpSenderThread;
    private RtcpSenderThread rtcpSenderThread;
    private RtcpGenerationThread rtcpGenerationThread;
    private LossBasedBandwidthEstimationThread lossBasedBandwidthEstimationThread;
    public static long timer;

    public MediaManager() {
        this.rtpSenderThread = new RtpSenderThread(NetworkConfigurations.getInstance().getUniversalIpAddress(), NetworkConfigurations.getInstance().getRtpSendPort());
        this.rtcpSenderThread = new RtcpSenderThread(NetworkConfigurations.getInstance().getUniversalIpAddress(), NetworkConfigurations.getInstance().getRtcpSendPort());
        this.rtcpGenerationThread = new RtcpGenerationThread();
        this.lossBasedBandwidthEstimationThread = new LossBasedBandwidthEstimationThread();
        this.timer = System.currentTimeMillis();
    }

    public static MediaManager getInstance() {
        if (mediaManager == null) {
            mediaManager = new MediaManager();
        }
        return mediaManager;
    }

    public static synchronized void startNetworkTransmission() {
        MediaManager.getInstance().lossBasedBandwidthEstimationThread.start();
        MediaManager.getInstance().rtpSenderThread.start();
        MediaManager.getInstance().rtcpSenderThread.start();
        MediaManager.getInstance().rtcpGenerationThread.start();
    }

    public RtpSenderThread getRtpSenderThread() {
        return this.rtpSenderThread;
    }

    public RtcpSenderThread getRtcpSenderThread() {
        return this.rtcpSenderThread;
    }

    public LossBasedBandwidthEstimationThread getLossBasedBandwidthEstimationThread() {
        return this.lossBasedBandwidthEstimationThread;
    }

    public void shutDown() {
        if (rtpSenderThread != null) {
            rtpSenderThread.shutDown();
            rtpSenderThread = null;
        }

        if (rtcpSenderThread != null) {
            rtcpSenderThread.shutDown();
            rtcpSenderThread = null;
        }
    }
}
