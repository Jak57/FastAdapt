package com.reve.audio.util;

import com.reve.audio.AudioFrameBytePacket;
import com.reve.util.ShortArrayUtil;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class AudioFileWriterUtil {

    public static AudioFileWriterUtil audioFileWriterUtil = null;

    public AudioFileWriterUtil() {

    }

    public static AudioFileWriterUtil getInstance() {
        if (audioFileWriterUtil == null) {
            audioFileWriterUtil = new AudioFileWriterUtil();
        }
        return  audioFileWriterUtil;
    }

    public void writeToFile(short[] encodeShortBuffer, int shortLen, byte[] tempByteData, FileOutputStream fos) {
        int byteLen = ShortArrayUtil.convertShortToByte(encodeShortBuffer, 0, shortLen, tempByteData, 0);
        try {
            fos.write(tempByteData, 0, byteLen);
        } catch (IOException ignored) {}
    }

    public void writeToCsvFile(int data, FileWriter fos) {
        try {
            fos.write(Integer.toString(data) + "\n");
            fos.flush();
        } catch (IOException ignored) {}
    }

    public void writeToCsvFile(AudioFrameBytePacket frame, FileWriter fos) {
        try {
            fos.write(Integer.toString(frame.sequenceNumber) +"," + Integer.toString(frame.len) + "\n");
            fos.flush();
        } catch (IOException ignored) {}
    }

    public void writeToBinaryFile(byte[] data, int offset, int len, FileOutputStream fos) {
        try {
            fos.write(data, offset, len);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
