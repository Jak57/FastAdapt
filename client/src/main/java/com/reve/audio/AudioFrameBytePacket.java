package com.reve.audio;

import com.reve.network.udp.rtp.RtpPacket;
import com.reve.util.SequenceUtils;

import java.util.concurrent.LinkedBlockingQueue;

public class AudioFrameBytePacket extends RtpPacket {

    public static final LinkedBlockingQueue<AudioFrameBytePacket> freeFrames = new LinkedBlockingQueue<>();


    private AudioFrameBytePacket(int len) {
        data = new byte[2048];
        this.len = len;
        this.sequenceNumber = -1;
    }

    public static AudioFrameBytePacket getNewFrame(int len) {
        AudioFrameBytePacket frame = freeFrames.poll();

        if (frame == null) {
            frame = new AudioFrameBytePacket(len);
        } else {
            frame.len = len;
        }

        if (frame.len > frame.data.length) {
            frame.data = new byte[frame.len];
        }
        return frame;
    }

    public void copy(byte[] data, int len) {
        this.len = len;
        System.arraycopy(data, 0, this.data, 0, len);
    }

    public static void freeFrame(AudioFrameBytePacket frame) {
        if (frame == null) {
            return;
        }

        frame.sequenceNumber = -1;
        frame.len = 0;
        freeFrames.offer(frame);
    }

    @Override
    public int compareTo(RtpPacket that) {
        return SequenceUtils.compareMin(this.sequenceNumber, that.sequenceNumber);
    }

    @Override
    public void onDestroy() {
        freeFrame(this);
    }
}
