package com.android.personbest.SavedDataManager;

import com.android.personbest.StepCounter.IStatistics;

import java.util.List;

public interface SavedDataManager {
    int getYesterdaySteps(int day);
    List<IStatistics> getLastWeekSteps(int day);
}
