package com.android.personbest;

public class IntentionalWalkUtils {
    public static final double STRIDE_CONST = 0.413; // Height in inch mult by 0.413 is the average stride length
    public static final int FEET_ONE_MILE = 5280; // Num of feet in one mile

    // Returns Height from stride length
    public static double strideLength(double height) {
        return height * STRIDE_CONST / 12;
    }

    // Returns MPH
    public static double velocity(double height, int numSteps, long timeInSeconds) {
        // When starting, velocity is 0
        if(timeInSeconds == 0)
            return 0;
        double distanceInMile = strideLength(height) * numSteps / FEET_ONE_MILE;
        double val = distanceInMile / (timeInSeconds / 3600.0);
        return val;
    }
}