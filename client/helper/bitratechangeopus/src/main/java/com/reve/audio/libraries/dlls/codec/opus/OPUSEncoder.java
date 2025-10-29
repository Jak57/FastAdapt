package com.reve.audio.libraries.dlls.codec.opus;

//import java.io.*;

import com.reve.util.LibraryLoader;

import java.io.File;

public class OPUSEncoder {

//    static{
//        File soFile = new File("libopus-1.4.so");
//        Runtime.getRuntime().load(soFile.getAbsolutePath());
//    }

    static{
            LibraryLoader.loadLibrary("lib/audio/opus");
//        System.loadLibrary("opus");
    }

    private static int CommonClassID = 0;
    private int classID;

    private native void open(int classID);

    private native int encode(int classID, short[] samples, int inOffset, int lenOfSamples, byte[] outputData, int outOffset);
//    private native int encodeWithBitRate(int classID, short[] samples, int inOffset, int lenOfSamples, byte[] outputData, int outOffset, int bitRate);

    private native void reset(int classID);
    private native void close(int classID);

    private native void setBitRate(int bitrate);
    private native void setSampleRate(int sampleRate);
    private native void setFrameTime(int time);

    private native void enableFEC(int classID, int packetDropPercentage);

    public OPUSEncoder(){
        classID = (CommonClassID++);
        open(classID);
    }
    public OPUSEncoder(int sampleRate, int bitrate, int frameTime){
        setBitRate(bitrate);
        setSampleRate(sampleRate);
        setFrameTime(frameTime);
        classID = (CommonClassID++);
        open(classID);
    }

    public int encode(short[] samples, int inOffset, int lenOfSamples, byte[] outputData, int outOffset) {
        int len = encode(classID, samples, inOffset, lenOfSamples, outputData, outOffset);
        return len;
    }

//    public int encodeWithBitRate(short[] samples, int inOffset, int lenOfSamples, byte[] outputData, int outOffset, int bitRate) {
//        int len = encodeWithBitRate(classID, samples, inOffset, lenOfSamples, outputData, outOffset, bitRate);
//        return len;
//    }

    public void enableFEC(int packetDropPercentage){
        enableFEC(classID, packetDropPercentage);
    }

    public void reset(){
        reset(classID);
    }

    public void close(){
        close(classID);
    }
}
