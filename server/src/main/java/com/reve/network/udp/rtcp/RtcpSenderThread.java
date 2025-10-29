package com.reve.network.udp.rtcp;

import com.reve.audio.configs.AudioConfigurations;
import com.reve.network.config.NetworkConfigurations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class RtcpSenderThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(RtcpSenderThread.class);
    private boolean running;

    public final LinkedBlockingQueue<RtcpPacket> rtcpFramesForSendingToClientQueue;
    private RtcpReceiverThread rtcpReceiverThread;

    public RtcpSenderThread() {
        this.running = false;
        this.rtcpFramesForSendingToClientQueue = new LinkedBlockingQueue<>();
        this.rtcpReceiverThread = new RtcpReceiverThread(NetworkConfigurations.getInstance().getUniversalIpAddress(), NetworkConfigurations.getInstance().getRtcpReceivePort());
    }

    @Override
    public void run() {
        running = true;

        if (AudioConfigurations.getInstance().getIsAudioDebug()) {
            logger.info("Stared RtcpSenderThread");
        }

        DatagramPacket reusableRtcpDatagramPacket = new DatagramPacket(
               new byte[NetworkConfigurations.getInstance().getMaxRtcpDatagramPacketSize()],
               NetworkConfigurations.getInstance().getMaxRTPDatagramPacketDataSize()
        );

        this.rtcpReceiverThread.start();

        while (running) {
            RtcpPacket frame = null;
            try {

                if (this.rtcpReceiverThread.socket == null) {
                    Thread.sleep(10);
                    continue;
                }

                frame = rtcpFramesForSendingToClientQueue.poll(NetworkConfigurations.getInstance().getRtcpKeepAliveIntervalInSeconds(), TimeUnit.SECONDS);

                if (frame != null) {
                    reusableRtcpDatagramPacket.setData(frame.data, 0, frame.len);

                    reusableRtcpDatagramPacket.setAddress(this.rtcpReceiverThread.srcAddress);
                    reusableRtcpDatagramPacket.setPort(this.rtcpReceiverThread.srcPort);

                    this.rtcpReceiverThread.socket.send(reusableRtcpDatagramPacket);

                    if (AudioConfigurations.getInstance().getIsAudioDebug()) {
                        logger.info("---------------> Sent rtcp packet length={} address={} port={}", frame.len, reusableRtcpDatagramPacket.getAddress(), reusableRtcpDatagramPacket.getPort());
                    }
                    RtcpPacket.freeFrame(frame);
                }

            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void pushRtcpFrameToBeSent(RtcpPacket frame) {
        this.rtcpFramesForSendingToClientQueue.offer(frame);
    }

    public void shutDown() {
        running = false;
        rtcpReceiverThread.shutDown();
        rtcpFramesForSendingToClientQueue.clear();
    }
}
