package com.reve.audio;

import com.reve.audio.configs.AudioConfigurations;
import com.reve.audio.libraries.dlls.codec.opus.OPUSDecoder;
import com.reve.network.udp.rtcp.RtcpSenderThread;
import com.reve.network.udp.rtp.RtpSenderThread;

public class AudioManager {

    public static AudioManager manager = null;
    private OPUSDecoder recorderDecoder;
    private RtcpSenderThread rtcpSenderThread;
    private RtpSenderThread rtpSenderThread;

    public AudioManager() {
        this.rtpSenderThread = new RtpSenderThread();
        this.rtcpSenderThread = new RtcpSenderThread();

    }

    public static AudioManager getInstance() {
        if (manager == null) {
            manager = new AudioManager();
        }
        return manager;
    }

    public void initializeRecorderDecoder() {
        recorderDecoder = new OPUSDecoder(AudioConfigurations.getInstance().getSampleRate(), AudioConfigurations.getInstance().getBitRate(), AudioConfigurations.getInstance().getProcessingTimeMs());
    }

    public int decodeRecordedFrame(AudioFrameBytePacket frame, short[] encodeShortBuffer) {
        int decodeDataLen = recorderDecoder.decode(frame.data, 0, frame.len, encodeShortBuffer, 0);
        return decodeDataLen;
    }

    public RtcpSenderThread getRtcpSenderThread() {
        return this.rtcpSenderThread;
    }

    public static synchronized void startNetworkTransmission() {
        AudioManager.getInstance().rtpSenderThread.start();
        AudioManager.getInstance().rtcpSenderThread.start();
    }

    public void shutDown() {
        if (rtpSenderThread != null) {
            rtpSenderThread.shutDown();
            rtpSenderThread = null;
        }
        if (rtcpSenderThread != null) {
            rtcpSenderThread.shutDown();
            rtcpSenderThread = null;
        }
    }
}
