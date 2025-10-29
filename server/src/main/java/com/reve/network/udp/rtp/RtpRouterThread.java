package com.reve.network.udp.rtp;

import com.reve.network.config.NetworkConfigurations;

import java.net.DatagramPacket;
import java.util.concurrent.LinkedBlockingQueue;

public class RtpRouterThread extends Thread {

    private boolean running;
    private static final LinkedBlockingQueue<DatagramPacket> receivedRTPDatagramPacketsQueue = new LinkedBlockingQueue<>();
    private static final LinkedBlockingQueue<DatagramPacket> freeRTPDatagramPackets = new LinkedBlockingQueue<>();

    private RtpParserThread rtpParserThread;

    public RtpRouterThread() {
        running = false;
        rtpParserThread = new RtpParserThread();
    }

    @Override
    public void run() {
        running = true;
        rtpParserThread.start();

        while (running) {
            DatagramPacket packet = null;
            try {
                packet = receivedRTPDatagramPacketsQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            routeRtpPacket(packet);
        }
    }

    public static DatagramPacket getRTPDatagramPacket() {
        DatagramPacket datagramPacket = freeRTPDatagramPackets.poll();
        if (datagramPacket == null) {
            return new DatagramPacket(new byte[NetworkConfigurations.getInstance().getMaxRTPDatagramPacketDataSize()], NetworkConfigurations.getInstance().getMaxRTPDatagramPacketDataSize());
        }
        return datagramPacket;
    }

    public static void freeRTPDatagramPackets(DatagramPacket datagramPacket) {
        if (datagramPacket == null) {
            return;
        }

        if (freeRTPDatagramPackets.size() > NetworkConfigurations.getInstance().getMaxFreeRtpDatagramPackets()) {
            datagramPacket = null;
            return;
        }
        freeRTPDatagramPackets.offer(datagramPacket);
    }

    public void pushToRTPReceivedPacketsQueue(DatagramPacket datagramPacket) {
        receivedRTPDatagramPacketsQueue.offer(datagramPacket);
    }

    public void routeRtpPacket(DatagramPacket packet) {
        this.rtpParserThread.pushToRtpChannelQueue(packet);
    }

    public void shutDown() {
        running = false;
        rtpParserThread.shutDown();
        receivedRTPDatagramPacketsQueue.clear();
        freeRTPDatagramPackets.clear();
    }
}
