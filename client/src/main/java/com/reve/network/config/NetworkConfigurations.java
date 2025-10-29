package com.reve.network.config;

public class NetworkConfigurations {

    private static NetworkConfigurations networkConfigurations = null;
    private String destinationAddress;
    private int destinationPort;
    private int rtpDatagramPacketMaxLength;
    private int rtcpDatagramPacketMaxLength;
    private int rtpKeepAliveIntervalInSeconds;
    private int rtcpKeepAliveIntervalInSeconds;
    private int rtcpPacketSendIntervalMs;
    private String rtcpSenderAddress;
    private int rtcpSenderPort;

    private int rtcpSendPort;
    private int rtcpReceivePort;

    private int rtpSendPort;
    private int rtpReceivePort;

    private String universalIpAddress;

    private int bitRateUpdateIntervalMs;

    public NetworkConfigurations() {
//        this.destinationAddress                    = "148.72.133.91";
//        this.destinationPort                       = 100;
//        this.rtcpSenderAddress                     = "148.72.133.91";
//        this.rtcpSenderPort                        = 200;

        this.destinationAddress                    = "127.0.0.1";
        this.destinationPort                       = 12345;
        this.rtcpSenderAddress                     = "127.0.0.1";
        this.rtcpSenderPort                        = 12345 - 2;

        this.universalIpAddress                    = "148.72.133.91";
//        this.universalIpAddress                    = "127.0.0.1";

        this.rtpSendPort                           = 9;
        this.rtpReceivePort                        = 9;
        this.rtcpSendPort                          = 19;
        this.rtcpReceivePort                       = 19;

        this.rtcpDatagramPacketMaxLength           = 2048;
        this.rtpKeepAliveIntervalInSeconds         = 5;
        this.rtcpKeepAliveIntervalInSeconds        = 5;
        this.rtpDatagramPacketMaxLength            = 2048;
        this.rtcpPacketSendIntervalMs              = 1000;

        this.bitRateUpdateIntervalMs               = 3000; // 5000
    }

    public static synchronized NetworkConfigurations getInstance() {
        if (networkConfigurations == null) {
            networkConfigurations = new NetworkConfigurations();
        }
        return networkConfigurations;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public int getDestinationPort() {
        return destinationPort;
    }

    public int getRtpDatagramPacketMaxLength() {
        return rtpDatagramPacketMaxLength;
    }

    public int getRtpKeepAliveIntervalInSeconds() {
        return rtpKeepAliveIntervalInSeconds;
    }

    public int getRtcpDatagramPacketMaxLength() {
        return rtcpDatagramPacketMaxLength;
    }

    public String getRtcpSenderAddress() {
        return rtcpSenderAddress;
    }

    public int getRtcpSenderPort() {
        return rtcpSenderPort;
    }

    public int getRtcpReceivePort() {
        return rtcpReceivePort;
    }

    public int getRtcpSendPort() {
        return rtcpSendPort;
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

    public int getRtcpPacketSendIntervalMs() {
        return rtcpPacketSendIntervalMs;
    }

    public int getRtcpKeepAliveIntervalInSeconds() {
        return rtcpKeepAliveIntervalInSeconds;
    }

    public int getBitRateUpdateIntervalMs() {
        return bitRateUpdateIntervalMs;
    }

    public static void setNetworkConfigurations(NetworkConfigurations networkConfigurations) {
        NetworkConfigurations.networkConfigurations = networkConfigurations;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public void setDestinationPort(int destinationPort) {
        this.destinationPort = destinationPort;
    }

    public void setRtpDatagramPacketMaxLength(int rtpDatagramPacketMaxLength) {
        this.rtpDatagramPacketMaxLength = rtpDatagramPacketMaxLength;
    }

    public void setRtpKeepAliveIntervalInSeconds(int rtpKeepAliveIntervalInSeconds) {
        this.rtpKeepAliveIntervalInSeconds = rtpKeepAliveIntervalInSeconds;
    }
}
