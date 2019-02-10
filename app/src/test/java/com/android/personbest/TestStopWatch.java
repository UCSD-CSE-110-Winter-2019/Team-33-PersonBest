package com.android.personbest;
import org.junit.Test;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.*;


public class TestStopWatch {
    @Test
    public void testTimeLapse1() throws InterruptedException {
        StopWatch sp = new StopWatch();
        sp.start();
        TimeUnit.SECONDS.sleep(60);
        long currTime = sp.takeTime();
        assertEquals("Expected: 60, got" + currTime, 60, currTime);
        sp.end();
    }

    @Test
    public void testTimeLapse2() throws InterruptedException {
        StopWatch sp = new StopWatch();
        sp.start();
        TimeUnit.SECONDS.sleep(5);
        long currTime = sp.takeTime();
        assertEquals("Expected: 5, got" + currTime, 5, currTime);
        sp.end();
    }

    @Test
    public void testTimeLapse3() throws InterruptedException {
        StopWatch sp = new StopWatch();
        sp.start();
        TimeUnit.SECONDS.sleep(0);
        long currTime = sp.takeTime();
        assertEquals("Expected: 0, got" + currTime, 0, currTime);
        sp.end();
    }

    @Test(expected = RuntimeException.class)
    public void testTimeLapseException() {
        StopWatch sp = new StopWatch();
        sp.start();
        sp.end();
        sp.takeTime();
    }
}
