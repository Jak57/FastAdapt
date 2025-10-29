package com.reve.network.udp.rtcp;

import com.reve.audio.configs.AudioConfigurations;
import com.reve.network.config.NetworkConfigurations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;
import java.util.concurrent.LinkedBlockingQueue;

public class RtcpRouterThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(RtcpRouterThread.class);
    private boolean running;

    private static final LinkedBlockingQueue<DatagramPacket> freeRtcpDatagramPackets = new LinkedBlockingQueue<>();
    private static final LinkedBlockingQueue<DatagramPacket> receivedRtcpDatagramPacketsQueue = new LinkedBlockingQueue<>();
    private RtcpParserThread rtcpParserThread;

    public RtcpRouterThread() {
        running = false;
        rtcpParserThread = new RtcpParserThread();
    }

    @Override
    public void run() {
        running = true;
        if (AudioConfigurations.getInstance().getIsAudioDebug()) {
            logger.info("Started RtcpRouterThread");
        }
        rtcpParserThread.start();

        while (running) {
            DatagramPacket packet = null;
            try {
                packet = receivedRtcpDatagramPacketsQueue.take();
                routeRtcpPacket(packet);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static DatagramPacket getRtcpDatagramPacket() {
        DatagramPacket datagramPacket = freeRtcpDatagramPackets.poll();
        if (datagramPacket == null) {
            return new DatagramPacket(
                    new byte[NetworkConfigurations.getInstance().getRtpDatagramPacketMaxLength()],
                    NetworkConfigurations.getInstance().getRtpDatagramPacketMaxLength()
            );
        }
        return datagramPacket;
    }

    public void routeRtcpPacket(DatagramPacket datagramPacket) {
        this.rtcpParserThread.pushToRtcpChannelQueue(datagramPacket);
    }

    public void pushToRtcpReceivedPacketsQueue(DatagramPacket datagramPacket) {
        receivedRtcpDatagramPacketsQueue.offer(datagramPacket);
    }

    public void shutDown() {
        running = false;
        rtcpParserThread.shutDown();
        freeRtcpDatagramPackets.clear();
        receivedRtcpDatagramPacketsQueue.clear();
    }
}
