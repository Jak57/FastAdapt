package com.reve.audio;

import com.reve.audio.util.AudioFileWriterThread;
import java.util.concurrent.LinkedBlockingQueue;

public class AudioProcessor extends Thread {

    public static AudioProcessor audioProcessor = null;
    private boolean running;
    private final LinkedBlockingQueue<AudioFrameBytePacket> parsedAudioFrames;
    private AudioFileWriterThread audioFileWriterThread;

    public AudioProcessor() {
        running = false;
        this.parsedAudioFrames = new LinkedBlockingQueue<>();
        this.audioFileWriterThread = new AudioFileWriterThread();
    }

    public static AudioProcessor getInstance() {
        if (audioProcessor == null) {
            audioProcessor = new AudioProcessor();
        }
        return audioProcessor;
    }

    @Override
    public void run() {
        running = true;
        audioFileWriterThread.start();

        while (running) {
            AudioFrameBytePacket frame = null;
            try {
                frame = parsedAudioFrames.take();
                audioFileWriterThread.pushToFramesForWritingToFilesQueue(frame);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void pushToParsedAudioFramesQueue(AudioFrameBytePacket frame) {
        parsedAudioFrames.offer(frame);
    }

    public void shutDown() {
        running = false;
        audioFileWriterThread.shutDown();
        parsedAudioFrames.clear();
    }
}
