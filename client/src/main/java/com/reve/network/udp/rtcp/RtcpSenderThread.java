package com.reve.network.udp.rtcp;

import com.reve.audio.configs.AudioConfigurations;
import com.reve.network.config.NetworkConfigurations;
import com.reve.network.udp.rtp.RtpSenderThread;
import com.reve.network.util.MediaManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class RtcpSenderThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(RtcpSenderThread.class);
    private boolean running;
    private final LinkedBlockingQueue<RtcpPacket> rtcpFrameTobeSent = new LinkedBlockingQueue<>();

    private InetAddress destAddress;
    private int destPort;
    public DatagramSocket socket;

    private RtcpReceiverThread rtcpReceiverThread;
    private RtcpGenerationThread rtcpGenerationThread;
    private DatagramPacket keepAliveRtcpDatagramPacket;
    private int count;

    public RtcpSenderThread(String destAddress, int destPort) {
        running = false;
        count = 0;
        try {
            socket = new DatagramSocket();
            this.destAddress = InetAddress.getByName(destAddress);
            this.destPort = destPort;
            rtcpReceiverThread = new RtcpReceiverThread(socket);
            rtcpGenerationThread = new RtcpGenerationThread();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        running = true;

        DatagramPacket reusableRtcpDatagramPacket = new DatagramPacket(
                new byte[NetworkConfigurations.getInstance().getRtcpDatagramPacketMaxLength()],
                NetworkConfigurations.getInstance().getRtcpDatagramPacketMaxLength()
        );
        reusableRtcpDatagramPacket.setAddress(destAddress);
        reusableRtcpDatagramPacket.setPort(destPort);

        rtcpReceiverThread.start();

        initializeKeepAliveRtcpDatagramPacket();
        try {
            socket.send(keepAliveRtcpDatagramPacket);
            if (AudioConfigurations.getInstance().getIsAudioDebug()) {
                logger.info("Sent Rtcp Keep Alive Datagram Packet address={} port={}", keepAliveRtcpDatagramPacket.getAddress(), keepAliveRtcpDatagramPacket.getPort());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (running) {

            try {
                RtcpPacket frame = rtcpFrameTobeSent.poll(NetworkConfigurations.getInstance().getRtcpKeepAliveIntervalInSeconds(), TimeUnit.SECONDS);

                if (frame != null) {
                    reusableRtcpDatagramPacket.setData(frame.data, 0, frame.len);
                    socket.send(reusableRtcpDatagramPacket);

                    count++;
                    int len = frame.len;

                    int sendingBitRate = RtpSenderThread.getSendingBitRate() * 8;
//                    pushSendingBitRateToQueue(sendingBitRate);

                    if (AudioConfigurations.getInstance().getIsAudioDebug()) {
                        logger.info("Sent Rtt frame length={} address={} port={} count={} sendingBitRate={} bps", len, reusableRtcpDatagramPacket.getAddress(), reusableRtcpDatagramPacket.getPort(), count, sendingBitRate);
                    }
                    RtcpPacket.freeFrame(frame);
                    RtpSenderThread.resetSendingBitRate();

                }
            } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
            }
        }
    }

    public void pushRtcpFrameToBeSent(RtcpPacket packet) {
        this.rtcpFrameTobeSent.offer(packet);
    }

    private void initializeKeepAliveRtcpDatagramPacket() {
        byte[] data = new byte[2];
        keepAliveRtcpDatagramPacket = new DatagramPacket(data, data.length);
        keepAliveRtcpDatagramPacket.setAddress(this.destAddress);
        keepAliveRtcpDatagramPacket.setPort(this.destPort);
        keepAliveRtcpDatagramPacket.setData(data, 0, data.length);
    }

//    public void pushSendingBitRateToQueue(int bitRate) {
//        MediaManager.getInstance().getLossBasedBandwidthEstimationThread().pushToSendingBitRateQueue(bitRate);
//    }

    public void shutDown() {
        running = false;
        rtcpReceiverThread.shutDown();
        rtcpFrameTobeSent.clear();
        socket.close();
        keepAliveRtcpDatagramPacket = null;
    }
}
