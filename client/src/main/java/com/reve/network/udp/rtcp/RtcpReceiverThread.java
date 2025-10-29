package com.reve.network.udp.rtcp;

import com.reve.audio.configs.AudioConfigurations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.*;

public class RtcpReceiverThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(RtcpReceiverThread.class);
    private boolean running;
    public DatagramSocket socket;
    private RtcpRouterThread rtcpRouterThread;

    public RtcpReceiverThread(DatagramSocket socket) {
        running = false;
        this.socket = socket;

        rtcpRouterThread = new RtcpRouterThread();
    }

    @Override
    public void run() {
        if (AudioConfigurations.getInstance().getIsAudioDebug()) {
            logger.info("Started RtcpReceiverThread.");
        }

        running = true;
        this.rtcpRouterThread.start();

        while (running) {
            try {
                DatagramPacket packet = RtcpRouterThread.getRtcpDatagramPacket();
                socket.receive(packet);
                int len = packet.getLength();

                if (AudioConfigurations.getInstance().getIsAudioDebug()) {
                    logger.info("Received Rtcp packet length is {} address={} port={}", len, packet.getAddress(), packet.getPort());
                }

//                if (packet.getLength() == 11) {
//                    RtcpPacketFactory.parseRtcpPacketRtt(packet, 1);
//                } else {
                    rtcpRouterThread.pushToRtcpReceivedPacketsQueue(packet);
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void shutDown() {
        running = false;
        socket.close();
        rtcpRouterThread.shutDown();
    }
}
