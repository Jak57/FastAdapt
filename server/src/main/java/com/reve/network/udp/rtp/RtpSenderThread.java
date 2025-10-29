package com.reve.network.udp.rtp;

import com.reve.audio.configs.AudioConfigurations;
import com.reve.network.config.NetworkConfigurations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;

public class RtpSenderThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(RtpSenderThread.class);
    private boolean running;
    private RtpReceiverThread rtpReceiverThread;

    public RtpSenderThread() {
        running = false;
        rtpReceiverThread = new RtpReceiverThread(NetworkConfigurations.getInstance().getUniversalIpAddress(), NetworkConfigurations.getInstance().getRtpSendPort());
    }

    @Override
    public void run() {
        running = true;
        DatagramPacket packet = new DatagramPacket(new byte[20], 20);

        if (AudioConfigurations.getInstance().getIsAudioDebug()) {
            logger.info("Started RtpSenderThread");
        }
        rtpReceiverThread.start();

        while (running) {
//            try {
//                if (this.rtpReceiverThread.srcAddress == null) {
//                    Thread.sleep(10);
//                    continue;
//                }
//
//                packet.setData(new byte[20], 0, 20);
//                packet.setAddress(this.rtpReceiverThread.srcAddress);
//                packet.setPort(this.rtpReceiverThread.srcPort);
//                this.rtpReceiverThread.socket.send(packet);
//
//                if (AudioConfigurations.getInstance().getIsAudioDebug()) {
//                    logger.info("Sent dummy Rtp packet lenght={} address={} port={}", packet.getLength(), packet.getAddress(), packet.getPort());
//                }
//                Thread.sleep(10000);
//            } catch (IOException | InterruptedException e) {
//                e.printStackTrace();
//            }

            try {
                if (this.rtpReceiverThread.srcAddress == null) {
                    Thread.sleep(10);
                    continue;
                }

                packet.setData(new byte[20], 0, 20);
                packet.setAddress(this.rtpReceiverThread.srcAddress);
                packet.setPort(this.rtpReceiverThread.srcPort);
//                this.rtpReceiverThread.socket.send(packet);

                if (AudioConfigurations.getInstance().getIsAudioDebug()) {
                    logger.info("Sent dummy Rtp packet lenght={} address={} port={}", packet.getLength(), packet.getAddress(), packet.getPort());
                }
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void shutDown() {
        running = false;
        rtpReceiverThread.shutDown();
    }
}
