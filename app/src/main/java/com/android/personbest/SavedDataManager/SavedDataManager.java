package com.android.personbest.SavedDataManager;

import com.android.personbest.StepCounter.IStatistics;

import java.util.List;

public interface SavedDataManager {
    int getYesterdaySteps(int day);
    int getStepsDaysBefore(int today, int days);
    int getYesterdayGoal(int day);
    int getGoalDaysBefore(int today, int days);
    List<IStatistics> getLastWeekSteps(int day);
}
