package com.reve.network.udp.rtp;

import com.reve.audio.AudioProcessor;

import java.net.DatagramPacket;
import java.util.concurrent.LinkedBlockingQueue;

public class RtpParserThread extends Thread {

    private final LinkedBlockingQueue<DatagramPacket> rtpChannelPacketsQueue;
    private boolean running;
    private AudioProcessor audioProcessor;

    public RtpParserThread() {
        this.rtpChannelPacketsQueue = new LinkedBlockingQueue<>();
        this.audioProcessor = AudioProcessor.getInstance();
        running = false;
    }

    @Override
    public void run() {
        running = true;
        audioProcessor.start();

        while (running) {
            DatagramPacket packet = null;
            try {
                packet = rtpChannelPacketsQueue.take();
                RtpPacketFactory.parseAudioPacket(packet);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void pushToRtpChannelQueue(DatagramPacket packet) {
        rtpChannelPacketsQueue.offer(packet);
    }

    public void shutDown() {
        running = false;
        audioProcessor.shutDown();
        rtpChannelPacketsQueue.clear();
    }
}
