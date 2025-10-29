package com.reve;

import com.reve.audio.AudioManager;
import com.reve.network.config.NetworkConfigurations;

public class Main {
    public static void main(String[] args) {
//         Command line arguments: rtpSendPort rtpReceivePort rtcpSendPort rtcpReceivePort

        int rtpSendPort;
        int rtpReceivePort;
        int rtcpSendPort;
        int rtcpReceivePort;

        rtpSendPort = Integer.valueOf(args[0]);
        rtpReceivePort = Integer.valueOf(args[1]);
        rtcpSendPort = Integer.valueOf(args[2]);
        rtcpReceivePort = Integer.valueOf(args[3]);

        NetworkConfigurations.getInstance().setRtpSendPort(rtpSendPort);
        NetworkConfigurations.getInstance().setRtpReceivePort(rtpReceivePort);
        NetworkConfigurations.getInstance().setRtcpSendPort(rtcpSendPort);
        NetworkConfigurations.getInstance().setRtcpReceivePort(rtcpReceivePort);

        AudioManager.startNetworkTransmission();
    }
}