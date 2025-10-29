package com.reve.network.udp.rtp;

import com.reve.audio.AudioManager;
import com.reve.audio.configs.AudioConfigurations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;

import static com.reve.network.udp.rtp.RtpRouterThread.getRTPDatagramPacket;

public class RtpReceiverThread extends Thread{

    private static final Logger logger = LoggerFactory.getLogger(RtpReceiverThread.class);

    private boolean running;
    public InetAddress srcAddress;
    public int srcPort;
    public DatagramSocket socket;
    private RtpRouterThread rtpRouterThread;
    private static int count;

    public RtpReceiverThread(String srcAddress, int srcPort) {
        running = false;
        this.count = 0;
        try {
            this.srcAddress = InetAddress.getByName(srcAddress);
            this.srcPort = srcPort;
            socket = new DatagramSocket(this.srcPort, this.srcAddress);

            if (AudioConfigurations.getInstance().getIsAudioDebug()) {
                logger.info("Successfully created Rtp socket");
            }
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }

        this.rtpRouterThread = new RtpRouterThread();
    }

    @Override
    public void run() {
        if (AudioConfigurations.getInstance().getIsAudioDebug()) {
            logger.info("Started Rtp receiver thread.");
        }
        running = true;

        this.rtpRouterThread.start();

        while (running) {
            try {
                DatagramPacket packet = getRTPDatagramPacket();
                socket.receive(packet);
                this.srcAddress = packet.getAddress();
                this.srcPort = packet.getPort();
                count += 1;

                int len = packet.getLength();
                if (AudioConfigurations.getInstance().getIsAudioDebug()) {
                    logger.info("Received Rtp packet length is {} count={}", len, count);
                }
                rtpRouterThread.pushToRTPReceivedPacketsQueue(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void shutDown() {
        running = false;
        rtpRouterThread.shutDown();
        socket.close();
    }
}
