package com.android.personbest.SavedDataManager;

import com.android.personbest.StepCounter.IStatistics;

import java.util.List;

// todayStr = theTimer.getTodayString()
public interface SavedDataManager {
    // local
    boolean isFirstTimeUser();
    void setFirstTimeUser(boolean isFirstTime);

    // return null if not exists
    // return id of a existing email in db
    String getIdByEmail(String email);

    // data to sync
    int getUserHeight();
    boolean setUserHeight(int height);

    int getStepsByDayStr(String day);
    boolean setStepsByDayStr(String day, int step);
    int getIntentionalStepsByDayStr(String day);
    boolean setIntentionalStepsByDayStr(String day, int step);

    long getExerciseTimeByDayStr(String day);
    boolean setExerciseTimeByDayStr(String day, long time);
    float getAvgMPHByDayStr(String day);
    boolean setAvgMPHByDayStr(String day, float mph);

    int getCurrentGoal();
    boolean setCurrentGoal(int goal);
    int getGoalByDayStr(String day);
    boolean setGoalByDayStr(String day, int goal);

    void clearData(); // clear everything

    IStatistics getStatByDayStr(String day);
//    boolean setStatByDayStr(String day, IStatistics stat); // might unnecessary

    // return list of IStatistics
    List<IStatistics> getLastWeekSteps(String day);
    // return a list of IStatistics
    // 28 days before the day in the argument
    List<IStatistics> getLastMonthStat(String day);
    // TODO
//    List<IStatistics> getFriendMonthlyStat(String email);

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
