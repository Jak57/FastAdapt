package com.reve.audio.io;

import com.reve.audio.AudioFrameBytePacket;
import com.reve.audio.AudioManager;
import com.reve.audio.configs.AudioConfigurations;
import com.reve.network.udp.rtcp.RtcpGenerationThread;
import com.reve.util.SequenceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class JavaRecorder extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(JavaRecorder.class);

    private boolean running;

    public JavaRecorder() {
        running = false;
    }

    @Override
    public void run() {
        running = true;
        int chunkSizeShort = AudioConfigurations.getInstance().getChunkSizeShort();
        int chunk_size_byte = chunkSizeShort * 2;

        String audioEncodingTestFilePath = AudioConfigurations.getInstance().getAudioEncodingTestFilePath();
        File micFile = new File(audioEncodingTestFilePath);
        FileInputStream micFis = null;
        try {
            micFis = new FileInputStream(micFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int micReadLength = 0;
        byte[] micByte = new byte[chunkSizeShort * 2];
        int sequenceNumber = SequenceUtils.MIN_VALID_SEQUENCE;

        AudioManager.getInstance().initializeRecorderEncoder();

        try {
            while (running) {
                micReadLength = micFis.read(micByte, 0, chunk_size_byte);
                if (micReadLength < chunk_size_byte) {
                    shutDown();
                }

                AudioFrameBytePacket frame = AudioFrameBytePacket.getNewFrame(micReadLength);
                frame.copy(micByte, micReadLength);

                frame.sequenceNumber = sequenceNumber;
                sequenceNumber = SequenceUtils.nextSequence(sequenceNumber);
//                frame.sequenceNumber = sequenceNumber;

                if (AudioConfigurations.getInstance().getIsAudioDebug()) {
                    logger.info("SequenceNumber={}", frame.sequenceNumber);
                }
                AudioManager.getInstance().encodeSendRecordedFrame(frame);

                Thread.sleep(AudioConfigurations.getInstance().getProcessingTimeMs() - 1);

            }
        } catch (Exception e) {
            if (micFis != null) {
                try {
                    micFis.close();
                } catch (IOException ignored) {
                }
            }
            shutDown();
            e.printStackTrace();
        }
    }

    public void shutDown() {
        running = false;
    }
}


// Reading from microphone

//package com.reve.audio.io;
//import com.reve.audio.AudioFrameBytePacket;
//import com.reve.audio.AudioManager;
//import com.reve.audio.configs.AudioConfigurations;
//import com.reve.network.udp.rtp.RtpSenderThread;
//import com.reve.util.SequenceUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.sound.sampled.*;
//
//public class JavaRecorder extends Thread {
//
//    private boolean running;
//    private static final Logger logger = LoggerFactory.getLogger(RtpSenderThread.class);
//
//    private static TargetDataLine micDataLine;
//
//    public JavaRecorder() {
//        running = false;
//        micDataLine = null;
//    }
//
//    @Override
//    public void run() {
//        running = true;
//
//        try {
//            AudioFormat format = AudioConfigurations.getInstance().getAudioFormat();
//            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
//            micDataLine = (TargetDataLine) AudioSystem.getLine(info);
//            micDataLine.open(format, AudioConfigurations.getInstance().getChunkSizeShort() * 2);
//
//            byte[] data = new byte[AudioConfigurations.getInstance().getChunkSizeShort() * 2];
//            byte[] emptyData = new byte[AudioConfigurations.getInstance().getChunkSizeShort() * 2];
//            int chunkSizeShort = AudioConfigurations.getInstance().getChunkSizeShort();
//            int readLen = 0;
//
//            int sequenceNumber = SequenceUtils.MIN_VALID_SEQUENCE;
//            AudioManager.getInstance().initializeRecorderEncoder();
//
//            micDataLine.start();
//            micDataLine.read(emptyData, 0, micDataLine.available()); // Clearing any unnecessary data before entering the recorder thread
//
//            while (running) {
//
//                long t1 = System.currentTimeMillis();
//                readLen = micDataLine.read(data, 0, data.length);
////                logger.info("1 - Time taken {} ms", (System.currentTimeMillis()-t1));
//                if (readLen < data.length) break;
//                AudioFrameBytePacket frame = AudioFrameBytePacket.getNewFrame(readLen);
//                frame.copy(data, readLen);
//
////                logger.info("2 Time taken {} ms", (System.currentTimeMillis()-t1));
//                sequenceNumber = SequenceUtils.nextSequence(sequenceNumber);
//                frame.sequenceNumber = sequenceNumber;
//                AudioManager.getInstance().encodeSendRecordedFrame(frame);
//
//                if (AudioConfigurations.getInstance().getIsAudioDebug()) {
//                    logger.info("Time taken {} ms", (System.currentTimeMillis()-t1));
//                }
//            }
//
//        } catch (LineUnavailableException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void shutDown() {
//        running = false;
//    }
//}
