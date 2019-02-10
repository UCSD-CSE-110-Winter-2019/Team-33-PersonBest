package com.android.personbest;

import com.android.personbest.stopwatch.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestStopwatch {
    @Test
    public void testTimeLapse1() {
        Stopwatch sp = new Stopwatch();
        TestWatchTime dummy = new TestWatchTime();
        sp.start(dummy);
        dummy.increment(5);
        long currTime = sp.takeTime();
        assertEquals("Expected: 5, got" + currTime, 5, currTime);
        sp.end();
    }

    @Test
    public void testTimeLapse2() {
        Stopwatch sp = new Stopwatch();
        TestWatchTime dummy = new TestWatchTime();
        sp.start(dummy);
        dummy.increment(0);
        long currTime = sp.takeTime();
        assertEquals("Expected: 0, got" + currTime, 0, currTime);
        sp.end();
    }

    @Test(expected = RuntimeException.class)
    public void testTimeLapseException() {
        Stopwatch sp = new Stopwatch();
        TestWatchTime dummy = new TestWatchTime();
        sp.start(dummy);
        sp.end();
        sp.takeTime();
    }
}