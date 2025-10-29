package com.reve.audio.util.log;

import com.reve.audio.configs.AudioConfigurations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

public class AudioLogWriterThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(AudioLogWriterThread.class);
    private final LinkedBlockingQueue<LogPacket> logFrameQueue = new LinkedBlockingQueue();
    private boolean running;

    private FileWriter csvWriter = null;

    public AudioLogWriterThread() {
        running = false;
        try {
            csvWriter = new FileWriter(AudioConfigurations.getInstance().getAudioLogFilePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        running = true;
        LogPacket frame;

        try {
            csvWriter.append("sequence_number,loss_percentage,bitrate\n");
            csvWriter.flush();

            while (running) {
                try {
                    frame = logFrameQueue.take();
                    if (AudioConfigurations.getInstance().getIsAudioDebug()) {
                        logger.info("LogInfo sequenceNo={} lossPercentage={}% bitRate={} bps", frame.sequenceNumber, frame.lossPercentage, frame.bitRate);
                    }

                    csvWriter.append(String.valueOf(frame.sequenceNumber)).append(",").append(String.valueOf(frame.lossPercentage)).append(",").append(String.valueOf(frame.bitRate)).append("\n");
                    csvWriter.flush();
                    LogPacket.freeFrame(frame);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pushToLogFrameQueue(LogPacket frame) {
        this.logFrameQueue.offer(frame);
    }

    public void shutDown() {
        running = false;
        logFrameQueue.clear();
        try {
            csvWriter.close();
        } catch (IOException ignored) {}
    }
}
