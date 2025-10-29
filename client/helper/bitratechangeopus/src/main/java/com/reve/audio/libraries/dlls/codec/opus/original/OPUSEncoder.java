package com.reve.audio.libraries.dlls.codec.opus.original;

//import com.reve.util.LibraryLoader;

public class OPUSEncoder {

    static{
//            LibraryLoader.loadLibrary("lib/audio/opus");
        System.loadLibrary("opusTest");
    }
    private static int CommonClassID = 0;
    private final int classID;

    private native void open(int classID);
    private native int encode(int classID, short[] samples, int inOffset, int lenOfSamples, byte[] outputData, int outOffset);
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
        return encode(classID, samples, inOffset, lenOfSamples, outputData, outOffset);
    }
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
