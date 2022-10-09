package com.example.locationalarm;

import android.content.Context;
import android.media.SoundPool;

public class SoundPlayer {

    private static SoundPool soundPool;
    private static int defaultSound;

    public SoundPlayer(Context context) {
        soundPool = new SoundPool.Builder()
                .setMaxStreams(2)
                .build();

        defaultSound = soundPool.load(context, R.raw.alarmsound, 1);
        // todo: when adding more sounds, add them here and add function for them
    }

    public static void playDefaultSound() {
        soundPool.play(defaultSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }
}
