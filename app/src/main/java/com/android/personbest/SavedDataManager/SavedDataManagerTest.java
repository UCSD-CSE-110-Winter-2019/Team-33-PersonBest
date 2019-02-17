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
}
