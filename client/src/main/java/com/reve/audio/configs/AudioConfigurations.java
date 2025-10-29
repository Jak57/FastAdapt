package com.reve.audio.configs;

import javax.sound.sampled.AudioFormat;

public class AudioConfigurations {

    private static AudioConfigurations audioConfigurations = null;
    private int sampleRate;
    private int bitRate;
    private int minimumBitRate;
    private int maximumBitRate;
    private int bitRateAdjustmentFactor;
    private int packetDropPercentage;
    private int processingTimeMs;
    private int channel;
    private String audioEncodingTestFilePath;
    private String audioLogFilePath;
    private boolean signed;
    private boolean bigEndian;
    private int sampleSizeInBits;
    private boolean isAudioDebug;
    private int amountOfTimeForStoringPacketStatusMs;

    private int encoderComplexity;
    private int dredPacketLossPercentage;
    private int dredDuration;

    public AudioConfigurations() {
        // Original
        this.sampleRate                                  = 48000;

        this.bitRate                                     = 32000;
        this.minimumBitRate                              = 8000;
        this.maximumBitRate                              = 48000;
        this.bitRateAdjustmentFactor                     = 1000;

        this.packetDropPercentage                        = 5;
        this.processingTimeMs                            = 10 * 2;
        this.channel                                     = 1;
        this.signed                                      = true;
        this.bigEndian                                   = false;
        this.sampleSizeInBits                            = 16;
        this.audioEncodingTestFilePath                   = "F:\\audio\\bitrate_control_loss_based_test\\input\\opus_demo_author_48k.raw";
        this.audioLogFilePath                            = "F:\\audio\\bitrate_control_loss_based_test\\output\\csv\\client_log\\log.csv";
        this.isAudioDebug                                = false;
        this.amountOfTimeForStoringPacketStatusMs        = 2000;

        this.encoderComplexity                           = 10;
        this.dredPacketLossPercentage                    = 20;
        this.dredDuration                                = 8;
    }

    public static synchronized AudioConfigurations getInstance() {
        if (audioConfigurations == null) {
            audioConfigurations = new AudioConfigurations();
        }
        return audioConfigurations;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public int getBitRate() {
        return bitRate;
    }

    public int getPacketDropPercentage() {
        return packetDropPercentage;
    }

    public int getProcessingTimeMs() {
        return processingTimeMs;
    }

    public int getChannel() {
        return channel;
    }

    public int getChunkSizeShort() {
        return (sampleRate * processingTimeMs * channel) / 1000;
    }

    public String getAudioEncodingTestFilePath() {
        return audioEncodingTestFilePath;
    }

    public AudioFormat getAudioFormat() {
        return new AudioFormat(this.sampleRate, this.sampleSizeInBits, this.channel, this.signed, this.bigEndian);
    }

    public int getPacketStatusByteArraySize() {
        return (int) Math.ceil(this.amountOfTimeForStoringPacketStatusMs / (this.processingTimeMs * 8.0));
    }

    public boolean getIsAudioDebug() {
        return isAudioDebug;
    }

    public int getMinimumBitRate() {
        return minimumBitRate;
    }

    public int getMaximumBitRate() {
        return maximumBitRate;
    }

    public int getBitRateAdjustmentFactor() {
        return bitRateAdjustmentFactor;
    }

    public String getAudioLogFilePath() {
        return audioLogFilePath;
    }

    public int getEncoderComplexity() {
        return encoderComplexity;
    }

    public int getDredPacketLossPercentage() {
        return dredPacketLossPercentage;
    }

    public int getDredDuration() {
        return dredDuration;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    public void setPacketDropPercentage(int packetDropPercentage) {
        this.packetDropPercentage = packetDropPercentage;
    }

    public void setProcessingTimeMs(int processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public void setAudioEncodingTestFilePath(String audioEncodingTestFilePath) {
        this.audioEncodingTestFilePath = audioEncodingTestFilePath;
    }

    public void setIsAudioDebug(boolean debug) {
        isAudioDebug = debug;
    }
}
