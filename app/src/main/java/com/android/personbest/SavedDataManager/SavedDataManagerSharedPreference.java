package com.android.personbest.SavedDataManager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.android.personbest.StepCounter.DailyStat;
import com.android.personbest.StepCounter.DateCalendar;
import com.android.personbest.StepCounter.IDate;
import com.android.personbest.StepCounter.IStatistics;
import com.android.personbest.Timer.ITimer;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SavedDataManagerSharedPreference implements SavedDataManager {
    private static final int GOAL_INIT = 0;
    private final int DEFAULT_STEPS = 0;
    private final int DEFAULT_GOAL = 5000;
    private final float DEFAULT_MPH = 0;
    private final Long DEFAULT_TIME = 0L;
    private final String TAG = "SavedDataManagerSharedPreference";

    private Activity activity;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public SavedDataManagerSharedPreference(Activity activity) {
        this.activity = activity;
        this.sp = activity.getSharedPreferences("user_data",Context.MODE_PRIVATE);
        this.editor = sp.edit();
    }

    public String getIdByEmail(String email) {
        return null;
    }

    public int getYesterdaySteps(int day){
        SharedPreferences sp = activity.getSharedPreferences("user_data",Context.MODE_PRIVATE);
        IDate iDate = new DateCalendar(day);
        Integer yesterday = iDate.getYesterday();
        return sp.getInt(yesterday.toString() + "_TotalSteps",DEFAULT_STEPS);
    }


    public int getStepsDaysBefore(int today, int days) {
        SharedPreferences sp = activity.getSharedPreferences("user_data",Context.MODE_PRIVATE);
        Integer targetDay = today;
        while(days-->0) {
            IDate iDate = new DateCalendar(targetDay);
            targetDay = iDate.getYesterday();
        }
        return sp.getInt(targetDay.toString() + "_TotalSteps",DEFAULT_STEPS);
    }

    public int getYesterdayGoal(int day){
        SharedPreferences sp = activity.getSharedPreferences("user_data",Context.MODE_PRIVATE);
        IDate iDate = new DateCalendar(day);
        Integer yesterday = iDate.getYesterday();
        return sp.getInt(yesterday.toString() + "_Goal", DEFAULT_GOAL);
    }

    public int getGoalDaysBefore(int today, int days) {
        SharedPreferences sp = activity.getSharedPreferences("user_data",Context.MODE_PRIVATE);
        Integer targetDay = today;
        while(days-->0) {
            IDate iDate = new DateCalendar(targetDay);
            targetDay = iDate.getYesterday();
        }
        return sp.getInt(targetDay.toString() + "_Goal",DEFAULT_GOAL);
    }

    public List<IStatistics> getLastWeekSteps(int day){
        SharedPreferences sp = activity.getSharedPreferences("user_data",Context.MODE_PRIVATE);
        List<IStatistics> result = new ArrayList<>();
        for (Integer d = 1; d <= day; d++){
            int totalSteps = sp.getInt(d.toString() + "_TotalSteps",DEFAULT_STEPS);
            int intentionalSteps = sp.getInt(d.toString()+"_IntentionalSteps",DEFAULT_STEPS);
            int goal = sp.getInt(d.toString()+"_Goal",DEFAULT_GOAL);
            Float MPH = sp.getFloat(d.toString()+"_AverageMPH",DEFAULT_MPH);
            Long timewalked = sp.getLong(d.toString()+"_ExerciseTime",DEFAULT_TIME);
            DailyStat dailyStat = new DailyStat(goal,totalSteps,intentionalSteps,timewalked,MPH);
            result.add(dailyStat);
        }

        return result;

    }

    public boolean isFirstTimeUser() {
        return (sp.getAll().isEmpty());
    }
    public void setFirstTimeUser(boolean isFirstTime) {
        editor.putBoolean("is_first_time_user", false);
    }

    // data to sync

    public int getUserHeight() {
        return sp.getInt("Height", 0);
    }
    public boolean setUserHeight(int height) {
        editor.putInt("Height", height);
        editor.apply();
        return true;
    }

    public int getStepsByDayStr(String day) {
        return sp.getInt("total_steps:"+day, DEFAULT_STEPS);
    }
    public boolean setStepsByDayStr(String day, int step) {
        editor.putInt("total_steps:"+day, step);
        editor.apply();
        return true;
    }

    public int getIntentionalStepsByDayStr(String day) {
        return sp.getInt("intentional_steps:" + day, DEFAULT_STEPS);
    }
    public boolean setIntentionalStepsByDayStr(String day, int step) {
        editor.putInt("intentional_steps:" + day, step);
        editor.apply();
        return true;
    }

    public long getExerciseTimeByDayStr(String day) {
        return sp.getLong("exercise_time:" + day, 0);
    }

    public boolean setExerciseTimeByDayStr(String day, long time) {
        editor.putLong("exercise_time:" + day, time);
        editor.apply();
        return true;
    }

    public float getAvgMPHByDayStr(String day) {
        return sp.getLong("average_mph:" + day, 0);
    }
    public boolean setAvgMPHByDayStr(String day, float mph) {
        editor.putFloat("average_mph:" + day, mph);
        editor.apply();
        return true;
    }

    public int getCurrentGoal() {
        return sp.getInt("goal_current:", GOAL_INIT);
    }
    public boolean setCurrentGoal(int goal) {
        editor.putInt("goal_current:", goal);
        editor.apply();
        return true;
    }

    // if today not in db, use current goal
    public int getGoalByDayStr(String day) {
        return sp.getInt("goal:"+day, getCurrentGoal());
    }
    public boolean setGoalByDayStr(String day, int goal) {
        editor.putInt("goal:"+day, goal);
        editor.apply();
        return true;
    }

    public void clearData() {
        editor.clear();
        editor.apply();
    }

    // note: if not exist, will use default
    // might need to change in the future
    public IStatistics getStatByDayStr(String day) {
        int totalSteps = this.getStepsByDayStr(day);
        int goal = this.getStepsByDayStr(day);

        int intentionalSteps = this.getIntentionalStepsByDayStr(day);
        Float MPH = this.getAvgMPHByDayStr(day);
        Long timeWalked = this.getExerciseTimeByDayStr(day);

        return new DailyStat(goal,totalSteps,intentionalSteps,timeWalked,MPH);
    }
    public boolean setStatByDayStr(String day, IStatistics stat) {
        this.setStepsByDayStr(day, stat.getTotalSteps());
        this.setGoalByDayStr(day, stat.getGoal());
        this.setIntentionalStepsByDayStr(day, stat.getIntentionalSteps());
        this.setAvgMPHByDayStr(day, stat.getAverageMPH());
        this.setExerciseTimeByDayStr(day, stat.getTimeWalked());

        return true;
    }

    public List<IStatistics> getLastMonthStat(String day) {
        List<String> days = ITimer.getLastMonthDayStrings(day);
        ArrayList<IStatistics> toReturn = new ArrayList<>();

        for(String d : days) {
            toReturn.add(this.getStatByDayStr(d));
        }

        return toReturn;
    }

    public boolean isShownGoal(String today) {
       return sp.getString("last_day_prompted_goal","").equals(today);
    }
    public void setShownGoal(String today){
        Log.i(TAG,"Set day " + today + " to be last-day-shown-goal ");
        editor.putString("last_day_prompted_goal",today);
        editor.apply();
    }

    public boolean isShownSubGoal(String today){
        return sp.getString("last_day_prompted_sub_goal","").equals(today);
    }
    public void setShownSubGoal(String today){
        Log.i(TAG,"Set day " + today + " to be last-day-shown-sub-goal ");
        editor.putString("last_day_prompted_sub_goal",today);
        editor.apply();
    }

    public boolean isCheckedYesterdayGoal(String today) {
       return sp.getString("last_day_checked_yesterday_goal", "").equals(today);
    }

    public void setCheckedYesterdayGoal(String today) {
        Log.i(TAG,"Set day " + today + " to be last-day-checked-yesterday-goal ");
        editor.putString("last_day_checked_yesterday_goal",today);
        editor.apply();
    }

    public boolean isShownYesterdayGoal(String today) {
        return sp.getString("last_day_prompted_yesterday_goal","").equals(today);
    }
    public void setShownYesterdayGoal(String today) {
        Log.i(TAG,"Set day " + today + " to be last-day-prompted-yesterday-goal ");
        editor.putString("last_day_prompted_yesterday_goal",today);
        editor.apply();
    }

    public boolean isCheckedYesterdaySubGoal(String today) {
       return sp.getString("last_day_checked_yesterday_sub_goal", "").equals(today);
    }

    public void setCheckedYesterdaySubGoal(String today) {
        Log.i(TAG,"Set day " + today + " to be last-day-checked-yesterday-sub-goal ");
        editor.putString("last_day_checked_yesterday_sub_goal",today);
        editor.apply();
    }

    public boolean isShownYesterdaySubGoal(String today) {
        return sp.getString("last_day_prompted_yesterday_sub_goal","").equals(today);
    }
    public void setShownYesterdaySubGoal(String today) {
        Log.i(TAG,"Set day " + today + " to be last-day-shown-yesterday-sub-goal ");
        editor.putString("last_day_prompted_yesterday_sub_goal",today);
        editor.apply();
    }
}
