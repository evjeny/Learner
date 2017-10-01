package com.evjeny.learner.models;

import android.content.Context;
import android.provider.Settings;
import android.widget.Chronometer;

/**
 * Created by Evjeny on 05.07.2017 18:49.
 */

public class Timer {
    private Chronometer chronometer;

    private long start = 0;
    private long stop = 0;

    public Timer(Context context) {
        chronometer = new Chronometer(context);
    }
    public void start() {
        start = System.currentTimeMillis();
    }
    public long stop() {
        stop = System.currentTimeMillis();
        return stop - start;
    }
}
