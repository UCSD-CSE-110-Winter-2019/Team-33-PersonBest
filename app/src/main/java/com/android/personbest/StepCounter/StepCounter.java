package com.android.personbest.StepCounter;

import java.util.List;

public interface StepCounter {
    int getRequestCode();
    void setup();
    void updateStepCount();
    int getYesterdaySteps();
    List<IStatistics> getLastWeekSteps();
}
