package com.android.personbest.SavedDataManager;

import com.android.personbest.StepCounter.IStatistics;

import java.util.List;

public interface SavedDataManager {
    int getYesterdaySteps(int day);
    int getStepsDaysBefore(int today, int days);
    int getYesterdayGoal(int day);
    int getGoalDaysBefore(int today, int days);
    int getTodaySteps(int day);
    List<IStatistics> getLastWeekSteps(int day);

    boolean isShownGoal(String today);
    void setShownGoal(String today);

    boolean isShownSubGoal(String today);
    void setShownSubGoal(String today);

    boolean isCheckedYesterdayGoal(String today);
    void setCheckedYesterdayGoal(String today);
    boolean isShownYesterdayGoal(String today);
    void setShownYesterdayGoal(String today);

    boolean isCheckedYesterdaySubGoal(String today);
    void setCheckedYesterdaySubGoal(String today);
    boolean isShownYesterdaySubGoal(String today);
    void setShownYesterdaySubGoal(String today);
}
