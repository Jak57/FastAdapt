package com.reve.network.udp.rtcp;

import com.reve.audio.configs.AudioConfigurations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;
import java.util.concurrent.LinkedBlockingQueue;

public class RtcpParserThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(RtcpParserThread.class);
    private boolean running;

    private final LinkedBlockingQueue<DatagramPacket> rtcpChannelPacketQueue;

    public RtcpParserThread() {
        running = false;
        rtcpChannelPacketQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void run() {
        running = true;
        if (AudioConfigurations.getInstance().getIsAudioDebug()) {
            logger.info("Started RtcpParserThread");
        }

        while (running) {
            DatagramPacket packet = null;
            try {
                packet = rtcpChannelPacketQueue.take();
                RtcpPacketFactory.parseRtcpPacket(packet);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void pushToRtcpChannelQueue(DatagramPacket packet) {
        rtcpChannelPacketQueue.offer(packet);
    }

    public void shutDown() {
        running = false;
        rtcpChannelPacketQueue.clear();
    }
}
