package com.android.personbest.stopwatch;

public final class TestWatchTime implements WatchTime {
    long now = 0;

    @Override
    public long nowInEpochSecond() {
        return now;
    }

    public void increment(long seconds) {
        now += seconds;
    }

    public void reset() {
        now = 0;
    }
}
