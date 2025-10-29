package com.reve.audio.libraries.dlls.codec.opus;//package com.reve.audio.libraries.dlls.codec.opus.original;

public class OPUSDecoder {
    static{
        System.loadLibrary("opus");
    }

    private static int CommonClassID = 0;
    private final int classID;

    public OPUSDecoder(){
        classID = (CommonClassID++);
        open(classID);
    }

    public OPUSDecoder(int sampleRate, int bitrate, int frameTime){
        setBitRate(bitrate);
        setSampleRate(sampleRate);
        setFrameTime(frameTime);
        classID = (CommonClassID++);
        open(classID);
    }

    private native void open(int classID);
    private native int decode(int classID, byte[] inputData, int inOffset, int length, short[] outputSample, int outOffset);
    private native int decodeLostPacket(int classID, byte[] inputData, int inOffset, int length, short[] outputSample, int outOffset, int outputBufferSize);
    private native void reset(int classID);
    private native void close(int classID);
    private native void setBitRate(int bitrate);
    private native void setSampleRate(int sampleRate);
    private native void setFrameTime(int time);

    public int decode(byte[] inputData, int inOffset, int length, short[] outputSample, int outOffset){
        int res = decode(classID, inputData, inOffset, length, outputSample, outOffset);
        return res;
    }
    public int decodeLostPacket(byte[] inputData, int inOffset, int length, short[] outputSample, int outOffset){
        int res = decodeLostPacket(classID, inputData, inOffset, length, outputSample, outOffset, outputSample.length - outOffset);
        return res;
    }

    public void reset(){
        reset(classID);
    }

    public void close(){
        close(classID);
    }

}
