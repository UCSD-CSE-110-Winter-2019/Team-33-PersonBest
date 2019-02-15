package com.android.personbest.StepCounter;

import java.util.List;

public interface StepCounter {
    int getRequestCode();
    void setup();
    void updateStepCount();
    int getYesterdaySteps(int day);
    List<IStatistics> getLastWeekSteps(int day);
}
