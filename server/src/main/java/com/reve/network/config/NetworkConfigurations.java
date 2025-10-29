package com.reve.network.config;

public class NetworkConfigurations {

    private static NetworkConfigurations networkConfigurations = null;
    private String srcAddress;
    private int srcPort;
    private int maxRTPDatagramPacketDataSize;
    private int maxFreeRtpDatagramPackets;
    private int rtcpKeepAliveIntervalInSeconds;
    private int maxRtcpDatagramPacketSize;

    private String rtcpReceiverAddress;
    private int rtcpReceiverPort;

    private int rtcpSendPort;
    private int rtcpReceivePort;

    private int rtpSendPort;
    private int rtpReceivePort;

    private String universalIpAddress;

    public NetworkConfigurations() {
        this.srcAddress                                             = "127.0.0.1";
        this.srcPort                                                = 12345;
        this.rtcpReceiverAddress                                    = "127.0.0.1";
        this.rtcpReceiverPort                                       = 12345 - 2;


        this.universalIpAddress                                     = "148.72.133.91";
//        this.universalIpAddress                                     = "127.0.0.1";


        this.rtpSendPort                                            = 9;
        this.rtpReceivePort                                         = 9;
        this.rtcpSendPort                                           = 19;
        this.rtcpReceivePort                                        = 19;

//        this.rtpSendPort                                            = 20;
//        this.rtpReceivePort                                         = 19;

//        this.srcAddress                                             = "148.72.133.91";
//        this.srcPort                                                = 100;
//        this.rtcpReceiverAddress                                    = "148.72.133.91";
//        this.rtcpReceiverPort                                       = 200;

        this.maxRTPDatagramPacketDataSize                           = 2048;
        this.maxFreeRtpDatagramPackets                              = 1000;
        this.rtcpKeepAliveIntervalInSeconds                         = 5;
        this.maxRtcpDatagramPacketSize                              = 2048;

//        this.rtcpReceiverAddress                                    = "127.0.0.1";
//        this.rtcpReceiverPort                                       = 12345 - 1;

//        this.rtcpReceiverAddress                                    = "148.72.133.91";
//        this.rtcpReceiverPort                                       = 200;
    }

    public static synchronized NetworkConfigurations getInstance() {
        if (networkConfigurations == null) {
            networkConfigurations = new NetworkConfigurations();
        }
        return networkConfigurations;
    }

    public String getSrcAddress() {
        return srcAddress;
    }

    public int getMaxFreeRtpDatagramPackets() {
        return maxFreeRtpDatagramPackets;
    }

    public int getSrcPort() {
        return srcPort;
    }

    public int getMaxRTPDatagramPacketDataSize() {
        return maxRTPDatagramPacketDataSize;
    }

    public int getRtcpKeepAliveIntervalInSeconds() {
        return rtcpKeepAliveIntervalInSeconds;
    }

    public int getMaxRtcpDatagramPacketSize() {
        return maxRtcpDatagramPacketSize;
    }

    public String getRtcpReceiverAddress() {
        return rtcpReceiverAddress;
    }

    public int getRtcpReceiverPort() {
        return rtcpReceiverPort;
    }

    public int getRtcpSendPort() {
        return rtcpSendPort;
    }

    public int getRtcpReceivePort() {
        return rtcpReceivePort;
    }

    public int getRtpSendPort() {
        return rtpSendPort;
    }

    public int getRtpReceivePort() {
        return rtpReceivePort;
    }

    public String getUniversalIpAddress() {
        return universalIpAddress;
    }

    public void setSrcAddress(String srcAddress) {
        this.srcAddress = srcAddress;
    }

    public void setSrcPort(int srcPort) {
        this.srcPort = srcPort;
    }

    public void setMaxRTPDatagramPacketDataSize(int maxRTPDatagramPacketDataSize) {
        this.maxRTPDatagramPacketDataSize = maxRTPDatagramPacketDataSize;
    }

    public void setRtcpReceiverPort(int rtcpReceiverPort) {
        this.rtcpReceiverPort = rtcpReceiverPort;
    }

    public void setRtpSendPort(int rtpSendPort) {
        this.rtpSendPort = rtpSendPort;
    }

    public void setRtpReceivePort(int rtpReceivePort) {
        this.rtpReceivePort = rtpReceivePort;
    }

    public void setRtcpSendPort(int rtcpSendPort) {
        this.rtcpSendPort = rtcpSendPort;
    }

    public void setRtcpReceivePort(int rtcpReceivePort) {
        this.rtcpReceivePort = rtcpReceivePort;
    }
}
