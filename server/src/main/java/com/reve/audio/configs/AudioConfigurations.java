package com.reve.audio.configs;

import javax.sound.sampled.AudioFormat;

public class AudioConfigurations {

    private static AudioConfigurations audioConfigurations = null;
    private int sampleRate;
    private int bitRate;
    private int packetDropPercentage;
    private int processingTimeMs;
    private int channel;
    private String audioDecodingTestFilePath;
    private String audioDecodingTestBinaryFilePath;
    private String audioDecodingTestCsvFilePath;
    private boolean signed;
    private boolean bigEndian;
    private int sampleSizeInBits;
    private boolean isAudioDebug;
    private int rtcpPacketSendingIntervalMs;
    private int amountOfTimeForStoringPacketStatusMs;

    public AudioConfigurations() {
        this.sampleRate                                  = 48000;
        this.bitRate                                     = 48000;
        this.packetDropPercentage                        = 5;
        this.processingTimeMs                            = 10 * 2;
        this.channel                                     = 1;
        this.signed                                      = true;
        this.bigEndian                                   = false;
        this.sampleSizeInBits                            = 16;
        this.audioDecodingTestFilePath                   = "F:\\audio\\bitrate_control_loss_based_test\\send_packet\\input\\output.raw";
        this.isAudioDebug                                = false;
        this.audioDecodingTestBinaryFilePath             = "decoded_binary.bin";
        this.audioDecodingTestCsvFilePath                = "decoded_csv.csv";
        this.rtcpPacketSendingIntervalMs                 = 1000;
        this.amountOfTimeForStoringPacketStatusMs        = 2000;
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

    public String getAudioDecodingTestFilePath() {
        return audioDecodingTestFilePath;
    }

    public AudioFormat getAudioFormat() {
        return new AudioFormat(this.sampleRate, this.sampleSizeInBits, this.channel, this.signed, this.bigEndian);
    }

    public String getAudioDecodingTestBinaryFilePath() {
        return audioDecodingTestBinaryFilePath;
    }

    public String getAudioDecodingTestCsvFilePath() {
        return audioDecodingTestCsvFilePath;
    }

    public boolean getIsAudioDebug() {
        return isAudioDebug;
    }

    public int getRtcpPacketSendingIntervalMs() {
        return rtcpPacketSendingIntervalMs;
    }

    public int getPacketStatusByteArraySize() {
        return (int) Math.ceil(this.amountOfTimeForStoringPacketStatusMs / (this.processingTimeMs * 8.0));
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

    public void setAudioDecodingTestFilePath(String audioDecodingTestFilePath) {
        this.audioDecodingTestFilePath = audioDecodingTestFilePath;
    }

    public void setIsAudioDebug(boolean debug) {
        isAudioDebug = debug;
    }
}
