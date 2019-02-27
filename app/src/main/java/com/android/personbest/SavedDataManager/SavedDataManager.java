package com.android.personbest.SavedDataManager;

import com.android.personbest.StepCounter.IStatistics;

import java.util.List;

// todayStr = theTimer.getTodayString()
public interface SavedDataManager {
    // legacy API
    int getYesterdaySteps(int day);
    int getStepsDaysBefore(int today, int days);
    int getYesterdayGoal(int day);
    int getGoalDaysBefore(int today, int days);
    List<IStatistics> getLastWeekSteps(int day);

    // data to sync
    int getStepsByDayStr(String day);
    boolean setStepsByDayStr(String day, int step);
    int getGoalByDayStr(String day);
    boolean setGoalByDayStr(String day, int goal);
    IStatistics getStatByDayStr(String day);
    boolean setStatByDayStr(String day, IStatistics stat);

    // below data are local only
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
