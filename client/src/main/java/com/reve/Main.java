package com.reve;

import com.reve.audio.AudioManager;
import com.reve.network.util.MediaManager;

public class Main {
    public static void main(String[] args) {
        AudioManager.startAudioMedia();
        MediaManager.startNetworkTransmission();
//        AudioManager.startRtcpMedia();

    }
}