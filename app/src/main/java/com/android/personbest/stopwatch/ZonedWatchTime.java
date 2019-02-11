package com.android.personbest.stopwatch;

import java.time.ZonedDateTime;
import java.time.ZoneId;

public final class ZonedWatchTime implements WatchTime {
    @Override
    public long nowInEpochSecond() {
        return ZonedDateTime.now(ZoneId.systemDefault()).toEpochSecond();
    }
}
