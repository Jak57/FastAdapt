package com.reve.audio.util;

import com.reve.audio.AudioFrameBytePacket;
import com.reve.audio.configs.AudioConfigurations;
import com.reve.network.udp.rtcp.RtcpBuilderThread;

import java.io.*;
import java.util.concurrent.LinkedBlockingQueue;

public class AudioFileWriterThread extends Thread {

    private String audioDecodingTestBinaryFilePath;
    private String audioDecodingTestCsvFilePath;
    private boolean running;
    private FileOutputStream fos_binary;
    private FileWriter fos_csv;
    private final LinkedBlockingQueue<AudioFrameBytePacket> framesForWritingToFilesQueue;
    private RtcpBuilderThread rtcpBuilderThread;

    public AudioFileWriterThread() {
        running = false;
        fos_binary = null;
        fos_csv = null;
        framesForWritingToFilesQueue = new LinkedBlockingQueue<>();
        rtcpBuilderThread = new RtcpBuilderThread();
    }

    @Override
    public void run() {
        running = true;
        audioDecodingTestBinaryFilePath = AudioConfigurations.getInstance().getAudioDecodingTestBinaryFilePath();
        audioDecodingTestCsvFilePath = AudioConfigurations.getInstance().getAudioDecodingTestCsvFilePath();
        rtcpBuilderThread.start();

        try {
            fos_binary = new FileOutputStream(new File(audioDecodingTestBinaryFilePath));
            fos_csv = new FileWriter(audioDecodingTestCsvFilePath);
            fos_csv.append("sequence_number,length\n");
            fos_csv.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (running) {
            AudioFrameBytePacket frame = null;
            try {
                frame = framesForWritingToFilesQueue.take();

//                AudioFrameBytePacket cloneFrame = AudioFrameBytePacket.getCloneFrame(frame);
//                AudioFileWriterUtil.getInstance().writeToCsvFile(cloneFrame.len, fos_csv);
//                AudioFileWriterUtil.getInstance().writeToBinaryFile(cloneFrame.data, 0, cloneFrame.len, fos_binary);
//                AudioFrameBytePacket.freeFrame(cloneFrame);

//                AudioFileWriterUtil.getInstance().writeToCsvFile(frame.len, fos_csv);

                if (frame.sequenceNumber > 0 && frame.len > 0) {
                    AudioFileWriterUtil.getInstance().writeToCsvFile(frame, fos_csv);
                    AudioFileWriterUtil.getInstance().writeToBinaryFile(frame.data, 0, frame.len, fos_binary);
                }

                rtcpBuilderThread.pushToFrameForGeneratingRtcpQueue(frame);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void pushToFramesForWritingToFilesQueue(AudioFrameBytePacket frame) {
        framesForWritingToFilesQueue.offer(frame);
    }

    public void shutDown() {
        running = false;
        rtcpBuilderThread.shutDown();
        try {
            fos_binary.close();
            fos_csv.close();
        } catch (IOException ignored) {}
        framesForWritingToFilesQueue.clear();
    }
}

/* Decoding Using OPUS decoder */

/*
package com.reve.audio.util;

import com.reve.audio.AudioFrameBytePacket;
import com.reve.audio.AudioManager;
import com.reve.audio.configs.AudioConfigurations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.concurrent.LinkedBlockingQueue;

public class AudioFileWriterThread extends Thread {

    private String audioDecodingTestFilePath;
    private boolean running;
    private FileOutputStream fos;
    private final LinkedBlockingQueue<AudioFrameBytePacket> framesForWritingToFilesQueue;

    public AudioFileWriterThread() {
        running = false;
        fos = null;
        framesForWritingToFilesQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void run() {
        running = true;
        audioDecodingTestFilePath = AudioConfigurations.getInstance().getAudioDecodingTestFilePath();

        byte[] tempByteArray = new byte[AudioConfigurations.getInstance().getChunkSizeShort() * 2];
        short[] encodeShortBuffer = new short[AudioConfigurations.getInstance().getChunkSizeShort()];
        AudioManager.getInstance().initializeRecorderDecoder();

        try {
            fos = new FileOutputStream(new File(audioDecodingTestFilePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (running) {
            AudioFrameBytePacket frame = null;
            try {
                frame = framesForWritingToFilesQueue.take();
                int decodeDataLen = AudioManager.getInstance().decodeRecordedFrame(frame, encodeShortBuffer);
                AudioFileWriterUtil.getInstance().writeToFile(encodeShortBuffer, decodeDataLen, tempByteArray, fos);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void pushToFramesForWritingToFilesQueue(AudioFrameBytePacket frame) {
        framesForWritingToFilesQueue.offer(frame);
    }

    public void shutDown() {
        running = false;
    }
}
*/