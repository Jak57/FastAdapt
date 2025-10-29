package com.reve.network.udp.rtcp;

import com.reve.audio.AudioManager;
import com.reve.audio.configs.AudioConfigurations;
import com.reve.network.config.NetworkConfigurations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;

public class RtcpReceiverThread extends Thread{

    private static final Logger logger = LoggerFactory.getLogger(RtcpReceiverThread.class);
    private boolean running;
    public DatagramSocket socket;
    public InetAddress srcAddress;
    public int srcPort;

    public RtcpReceiverThread(String srcAddress, int srcPort) {
        running = false;
        try {
            this.srcAddress = InetAddress.getByName(srcAddress);
            this.srcPort = srcPort;
            this.socket = new DatagramSocket(NetworkConfigurations.getInstance().getRtcpReceivePort());

        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        running = true;

        if (AudioConfigurations.getInstance().getIsAudioDebug()) {
            logger.info("Started RtcpReceiverThread");
        }

        DatagramPacket packet = new DatagramPacket(
                new byte[NetworkConfigurations.getInstance().getMaxRtcpDatagramPacketSize()],
                NetworkConfigurations.getInstance().getMaxRtcpDatagramPacketSize()
        );

        while (running) {
            try {

                socket.receive(packet);
                this.srcAddress = packet.getAddress();
                this.srcPort = packet.getPort();

                pushBackRtcpPacket(packet);

                if (AudioConfigurations.getInstance().getIsAudioDebug()) {
                    logger.info("===============> Received dummy Rtcp packet length={} address={} port={}", packet.getLength(), packet.getAddress(), packet.getPort());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void pushBackRtcpPacket(DatagramPacket packet) {
        RtcpPacket frame = RtcpPacket.getNewFrame();
        System.arraycopy(packet.getData(), 0, frame.data, 0, packet.getLength());
        frame.len = packet.getLength();
        AudioManager.getInstance().getRtcpSenderThread().pushRtcpFrameToBeSent(frame);
    }

    public void shutDown() {
        running = false;
        socket.close();
    }
}
