package com.reve.network.udp.rtp;

import com.reve.audio.configs.AudioConfigurations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class RtpReceiverThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(RtpReceiverThread.class);
    private boolean running;
    public DatagramSocket socket;

    public RtpReceiverThread(DatagramSocket socket) {
        running = false;
        this.socket = socket;
    }

    @Override
    public void run() {
        running = true;

        if (AudioConfigurations.getInstance().getIsAudioDebug()) {
            logger.info("Started RtpReceiverThread");
        }

        DatagramPacket packet = new DatagramPacket(new byte[20], 20);

        while (running) {
            packet.setData(new byte[20]);
            try {
                socket.receive(packet);
                if (AudioConfigurations.getInstance().getIsAudioDebug()) {
                    logger.info("Received dummy Rtp packet length={}", packet.getLength());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void shutDown() {
        running = false;
    }
}
