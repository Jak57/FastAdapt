package com.reve.audio;

import com.reve.audio.configs.AudioConfigurations;
import com.reve.audio.io.JavaRecorder;
import com.reve.audio.libraries.dlls.codec.opus.OPUSEncoder;
import com.reve.network.udp.rtcp.RtcpGenerationThread;
import com.reve.network.udp.rtcp.RtcpPacket;
import com.reve.network.udp.rtcp.RtcpPacketFactory;
import com.reve.network.udp.rtp.RtpPacketFactory;
import com.reve.network.util.MediaManager;
import com.reve.util.ShortArrayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AudioManager {

    private static final Logger logger = LoggerFactory.getLogger(AudioManager.class);
    public static AudioManager manager = null;

    private JavaRecorder recorder;
    private OPUSEncoder recorderEncoder;
    private RtcpGenerationThread rtcpGenerationThread;

    private final short[] encodeShortBuffer;

    private AudioManager() {
        encodeShortBuffer = new short[AudioConfigurations.getInstance().getChunkSizeShort()];
    }

    public static synchronized AudioManager getInstance() {
        if (manager == null) {
            manager = new AudioManager();
        }
        return manager;
    }

    public static synchronized void startAudioMedia() {
        AudioManager.getInstance().startRecording();
    }

    public static synchronized void startRtcpMedia() {
        AudioManager.getInstance().startRtcpGeneration();
    }

    public synchronized void startRtcpGeneration() {
        if (rtcpGenerationThread == null) {
            rtcpGenerationThread = new RtcpGenerationThread();
            rtcpGenerationThread.start();
        }
    }

    public synchronized void startRecording() {
        if (recorder == null) {
            recorder = new JavaRecorder();
            recorder.start();
        }
    }

    public void initializeRecorderEncoder() {
        recorderEncoder = new OPUSEncoder(AudioConfigurations.getInstance().getSampleRate(), AudioConfigurations.getInstance().getBitRate(), AudioConfigurations.getInstance().getProcessingTimeMs());
        recorderEncoder.enableFEC(AudioConfigurations.getInstance().getPacketDropPercentage());
        int dredStatus = recorderEncoder.enableDRED(AudioConfigurations.getInstance().getEncoderComplexity(), AudioConfigurations.getInstance().getDredPacketLossPercentage(), AudioConfigurations.getInstance().getDredDuration());

        if (AudioConfigurations.getInstance().getIsAudioDebug()) {
            logger.info("Status of DRED={}", dredStatus);
        }
    }

    public void encodeSendRecordedFrame(AudioFrameBytePacket frame) {
        int shortLen = ShortArrayUtil.convertByteToShort(frame.data, 0, frame.len, encodeShortBuffer, 0);
        frame.len = recorderEncoder.encodeWithBitRate(encodeShortBuffer, 0, shortLen, frame.data, 0, AudioConfigurations.getInstance().getBitRate());
        prepareAndPushAudioRtpPacketToBeSent(frame);
    }

    public void prepareAndPushAudioRtpPacketToBeSent(AudioFrameBytePacket frame) {
        // Getting empty frame for building RTP packet
        AudioFrameBytePacket audio_rtpPacket = AudioFrameBytePacket.getNewFrame(frame.len);
        // Building RTP packet
        audio_rtpPacket.len = RtpPacketFactory.buildAudioPacket(audio_rtpPacket.data, frame);
        // Pushing RTP packet to container
        MediaManager.getInstance().getRtpSenderThread().pushRtpFrameToBeSent(audio_rtpPacket);
        // Freeing frame
        AudioFrameBytePacket.freeFrame(frame);
    }

    public void prepareRttRtcpPacketToBeSent(RtcpPacket frame, int sequenceNumber) {
        frame.baseSequenceNumber = sequenceNumber;
        frame.len = RtcpPacketFactory.buildRttPacket(frame);
        MediaManager.getInstance().getRtcpSenderThread().pushRtcpFrameToBeSent(frame);
    }

    public void shutDown() {
        if (recorder != null) {
            recorder.shutDown();
            recorder = null;
        }

        if (rtcpGenerationThread != null) {
            rtcpGenerationThread.shutDown();
            rtcpGenerationThread = null;
        }
    }
}
