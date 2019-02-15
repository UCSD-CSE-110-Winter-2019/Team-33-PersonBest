package com.android.personbest.SavedDataManager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import com.android.personbest.MainActivity;
import com.android.personbest.StepCounter.DailyStat;
import com.android.personbest.StepCounter.IDate;
import com.android.personbest.StepCounter.IStatistics;

import java.util.ArrayList;
import java.util.List;

public class SavedDataManagerSharedPreference implements SavedDataManager {
    private final int DEFAULT_STEPS = 0;
    private final int DEFAULT_GOAL = 5000;

    private Activity activity;

    public SavedDataManagerSharedPreference(Activity activity) {
        this.activity = activity;
    }
    public int getYesterdaySteps(int day){
        SharedPreferences sp = activity.getSharedPreferences("user_data",Context.MODE_PRIVATE);
        IDate iDate = new IDate(day);
        Integer yesterday = iDate.getYesterDay();
        int totalSteps = sp.getInt(yesterday.toString() + "_TotalSteps",DEFAULT_STEPS);
        return totalSteps;
    }

    public List<IStatistics> getLastWeekSteps(int day){
        SharedPreferences sp = activity.getSharedPreferences("user_data",Context.MODE_PRIVATE);
        List<IStatistics> result = new ArrayList<>();
        for (Integer d = 0; d <= day; d++){
            int totalSteps = sp.getInt(d.toString() + "_TotalSteps",DEFAULT_STEPS);
            int intentionalSteps = sp.getInt(d.toString()+"_IntentionalSteps",DEFAULT_STEPS);
            int goal = sp.getInt(d.toString()+"_Goal",DEFAULT_GOAL);
            DailyStat dailyStat = new DailyStat(goal,totalSteps,intentionalSteps,"");
            result.add(dailyStat);
        }

        return result;

    }
}
