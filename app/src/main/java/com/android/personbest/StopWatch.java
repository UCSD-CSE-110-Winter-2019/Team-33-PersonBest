package com.android.personbest;
import java.time.ZonedDateTime;
import java.time.ZoneId;

class StopWatch {
    long startTime = -1;

    /* Start stopwatch and get start time in epoch */
    public void start() {
        startTime = ZonedDateTime.now(ZoneId.systemDefault()).toEpochSecond();
    }

    /* Return time elapsed in current stopwatch session, throws RuntimeException if stopwatch is not active */
    public long takeTime() {
        if(startTime == -1)
            throw new RuntimeException("The stopwatch is not active.");
        return ZonedDateTime.now(ZoneId.systemDefault()).toEpochSecond() - startTime;
    }

    /* End stop watch */
    public void end() {
        startTime = -1;
    }

}
