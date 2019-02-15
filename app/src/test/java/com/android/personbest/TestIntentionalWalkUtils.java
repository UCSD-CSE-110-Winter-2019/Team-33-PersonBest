package com.android.personbest;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestIntentionalWalkUtils {
    @Test
    public void testStrideLength() {
        int height = 70; // Height of Julian Edelman
        double strideLength = height * 0.413 / 12;
        assertEquals("Expected: " + strideLength, strideLength,
                IntentionalWalkUtils.strideLength(height), 0.0001);
    }

    @Test
    public void testVelocity() {
        int height = 70; // Height of Julian Edelman
        int numSteps = 8192;
        long timeInSeconds = 32768L;
        double velocity = 70 * 0.413 / 12 * 8192/ 5280 / (32768 / 3600.0);
        velocity = Math.round(velocity * 10) / (double)10;
        assertEquals("Expected: " + velocity, velocity,
                IntentionalWalkUtils.velocity(height, numSteps, timeInSeconds), 0.0001);
    }

    @Test
    public void testVelocity2() {
        int height = 70; // Height of Julian Edelman
        int numSteps = 0;
        long timeInSeconds = 0L;
        double velocity = 0.0;
        assertEquals("Expected: " + velocity, velocity,
                IntentionalWalkUtils.velocity(height, numSteps, timeInSeconds), 0.0001);
    }
}
