package com.reve.network.udp.rtp;

import com.reve.audio.configs.AudioConfigurations;
import com.reve.network.config.NetworkConfigurations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class RtpSenderThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(RtpSenderThread.class);
    private boolean running;
    private InetAddress destAddress;
    private int destPort;
    private DatagramSocket socket;
    public static int sendingBitRate;

    private final LinkedBlockingQueue<RtpPacket> rtpFrameTobeSent = new LinkedBlockingQueue<>();
    private RtpReceiverThread rtpReceiverThread;
    public static int count;

    private DatagramPacket keepAliveRtcpDatagramPacket;

    public RtpSenderThread(String destAddress, int destPort) {
        running = false;
        count = 0;
        sendingBitRate = 0;

        try {
            socket = new DatagramSocket();
            this.destAddress = InetAddress.getByName(destAddress);
            this.destPort = destPort;
            rtpReceiverThread = new RtpReceiverThread(socket);
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }
    }

    public void pushRtpFrameToBeSent(RtpPacket packet) {
        this.rtpFrameTobeSent.offer(packet);
    }

    @Override
    public void run() {
        running = true;
        if (AudioConfigurations.getInstance().getIsAudioDebug()) {
            logger.info("Started RtpSenderThread");
        }

        rtpReceiverThread.start();

        DatagramPacket reusableRtpDatagramPacket = new DatagramPacket(
                new byte[NetworkConfigurations.getInstance().getRtpDatagramPacketMaxLength()],
                NetworkConfigurations.getInstance().getRtpDatagramPacketMaxLength()
        );
        reusableRtpDatagramPacket.setAddress(destAddress);
        reusableRtpDatagramPacket.setPort(destPort);

        initializeKeepAliveRtcpDatagramPacket();
        try {
            socket.send(keepAliveRtcpDatagramPacket);
            if (AudioConfigurations.getInstance().getIsAudioDebug()) {
                logger.info("Sent Rtp Keep Alive Datagram Packet address={} port={}", keepAliveRtcpDatagramPacket.getAddress(), keepAliveRtcpDatagramPacket.getPort());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (running) {
            try {
                RtpPacket rtpPacket = rtpFrameTobeSent.poll(NetworkConfigurations.getInstance().getRtpKeepAliveIntervalInSeconds(), TimeUnit.SECONDS);

                if (rtpPacket != null) {
                    reusableRtpDatagramPacket.setData(rtpPacket.data, 0, rtpPacket.len);
                    socket.send(reusableRtpDatagramPacket);

                    count++;
                    int len = rtpPacket.len;
                    sendingBitRate += len;

                    if (AudioConfigurations.getInstance().getIsAudioDebug()) {
                        logger.info("Sent encoded audio frame of byte length={} address={} port={} count={}", len, reusableRtpDatagramPacket.getAddress(), reusableRtpDatagramPacket.getPort(), count);
                    }
                    RtpPacket.free(rtpPacket);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void initializeKeepAliveRtcpDatagramPacket() {
        byte[] data = new byte[2];
        keepAliveRtcpDatagramPacket = new DatagramPacket(data, data.length);
        keepAliveRtcpDatagramPacket.setAddress(this.destAddress);
        keepAliveRtcpDatagramPacket.setPort(this.destPort);
        keepAliveRtcpDatagramPacket.setData(data, 0, data.length);
    }

    public void shutDown() {
        running = false;
        rtpReceiverThread.shutDown();
        socket.close();
        rtpFrameTobeSent.clear();
        keepAliveRtcpDatagramPacket = null;
    }

    public static int getSendingBitRate() {
        return sendingBitRate;
    }

    public static void resetSendingBitRate() {
        sendingBitRate = 0;
    }
}
