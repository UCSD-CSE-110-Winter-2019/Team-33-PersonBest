package com.android.personbest.StepCounter;

public interface IStatistics {
    //TODO Design Stats
    String getDayStr();
    void setDayStr(String dayStr);
    int getYearFromDayStr();
    int getMonthFromDayStr();
    int getDayFromDayStr();
    int getGoal();
    void setGoal(int goal);
    int getTotalSteps();
    void setTotalSteps(int totalSteps);
    int getIntentionalSteps();
    void setIntentionalSteps(int intentionalSteps);
    Long getTimeWalked();
    void setTimeWalked(Long timeWalked);
    float getAverageMPH();
    void setAverageMPH(float averageMPH);
    float getHours();
    void setHours(float hours);

    String getStats();
    int getIntentionalWalk();
    int getIncidentWalk();
}
