package com.android.personbest.stopwatch;

public class Stopwatch {
    private long startTime = -1;
    private WatchTime watchTime = null;

    /* Start stopwatch and get start time in epoch */
    public void start(WatchTime watchT) {
        watchTime = watchT;
        startTime = watchTime.nowInEpochSecond();
    }

    /* Return time elapsed in current stopwatch session, throws RuntimeException if stopwatch is not active */
    public long takeTime() {
        if(startTime == -1)
            throw new RuntimeException("The stopwatch is not active.");
        return watchTime.nowInEpochSecond() - startTime;
    }

    /* End stop watch */
    public void end() {
        startTime = -1;
        watchTime = null;
    }
}