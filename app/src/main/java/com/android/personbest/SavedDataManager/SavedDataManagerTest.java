package com.android.personbest.SavedDataManager;

import com.android.personbest.StepCounter.IStatistics;

import java.util.ArrayList;
import java.util.List;

public class SavedDataManagerTest implements SavedDataManager {
    List<IStatistics> steps;
    public SavedDataManagerTest(List<IStatistics> data) {
        steps = data;
    }
    @Override
    public int getYesterdaySteps(int day) {
        return -1;
    }
    @Override
    public int getYesterdayGoal(int day) {
        return -1;
    }
    @Override
    public List<IStatistics> getLastWeekSteps(int day) {
        List<IStatistics> toRet = new ArrayList<>();
        for(int i = 1; i <=day; ++i) {
            toRet.add(steps.get(i - 1));
        }
        return toRet;
    }
    public List<IStatistics> getLastMonthStat(String day) {
        return null;
    }
    public int getStepsDaysBefore(int today, int days) {
        return -1;
    }
    public int getGoalDaysBefore(int today, int days) {
        return -1;
    }
    public String getTodayString() {
        return null;
    }
    public String getYesterdayString() {
        return null;
    }

    public int getStepsByDayStr(String day) {
        return -1;
    }
    public boolean setStepsByDayStr(String day, int step) {
        return true;
    }
    public int getGoalByDayStr(String day) {
        return -1;
    }
    public boolean setGoalByDayStr(String day, int goal) {
        return true;
    }
    public IStatistics getStatByDayStr(String day) {
        return null;
    }
    public boolean setStatByDayStr(String day, IStatistics stat) {
        return true;
    }


    public boolean isShownGoal(String today) {
        return false;
    }
    public void setShownGoal(String today) {}

    public boolean isShownSubGoal(String today) {
        return false;
    }
    public void setShownSubGoal(String today) {}

    public boolean isCheckedYesterdayGoal(String today) {
        return false;
    }
    public void setCheckedYesterdayGoal(String today) {}
    public boolean isShownYesterdayGoal(String today) {
        return false;
    }
    public void setShownYesterdayGoal(String today) {}
    public boolean isCheckedYesterdaySubGoal(String today) {
        return false;
    }
    public void setCheckedYesterdaySubGoal(String today) {}
    public boolean isShownYesterdaySubGoal(String today) {
        return false;
    }
    public void setShownYesterdaySubGoal(String today) {}
}
