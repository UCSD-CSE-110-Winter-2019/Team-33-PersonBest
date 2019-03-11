package com.android.personbest.SavedDataManager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.android.personbest.StepCounter.DailyStat;
import com.android.personbest.StepCounter.IStatistics;
import com.android.personbest.Timer.ITimer;

import java.util.ArrayList;
import java.util.List;

public class SavedDataManagerSharedPreference implements SavedDataManager {
    private static final int GOAL_INIT = 0;
    private final int DEFAULT_STEPS = 0;
    private final int DEFAULT_GOAL = 5000;
    private final float DEFAULT_MPH = 0;
    private final Long DEFAULT_TIME = 0L;
    private final String TAG = "SavedDataManagerSharedPreference";

    private final static int DAYS_IN_WEEK = 7;

    private Activity activity;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public SavedDataManagerSharedPreference(Activity activity) {
        this.activity = activity;
        this.sp = activity.getSharedPreferences("user_data",Context.MODE_PRIVATE);
        this.editor = sp.edit();
    }

    public void getIdByEmail(String email, SavedDataOperatorString callback) {
        callback.op(null);
    }


    // data to sync

    public int getUserHeight(SavedDataOperatorInt callback) {
        return sp.getInt("Height", 0);
    }
    public void setUserHeight(int height, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp) {
        editor.putInt("Height", height);
        editor.apply();
    }

    public int getStepsByDayStr(String day, SavedDataOperatorInt callback) {
        return sp.getInt("total_steps:"+day, DEFAULT_STEPS);
    }
    public void setStepsByDayStr(String day, int step, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp) {
        editor.putInt("total_steps:"+day, step);
        editor.apply();
    }

    public int getIntentionalStepsByDayStr(String day, SavedDataOperatorInt callback) {
        return sp.getInt("intentional_steps:" + day, DEFAULT_STEPS);
    }
    public void setIntentionalStepsByDayStr(String day, int step, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp) {
        editor.putInt("intentional_steps:" + day, step);
        editor.apply();
    }

    public long getExerciseTimeByDayStr(String day, SavedDataOperatorLong callback) {
        return sp.getLong("exercise_time:" + day, 0);
    }

    public void setExerciseTimeByDayStr(String day, long time, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp) {
        editor.putLong("exercise_time:" + day, time);
        editor.apply();
    }

    public float getAvgMPHByDayStr(String day, SavedDataOperatorFloat callback) {
        return sp.getLong("average_mph:" + day, 0);
    }
    public void setAvgMPHByDayStr(String day, float mph, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp) {
        editor.putFloat("average_mph:" + day, mph);
        editor.apply();
    }

    public int getCurrentGoal(SavedDataOperatorInt callback) {
        return sp.getInt("goal_current:", GOAL_INIT);
    }
    public void setCurrentGoal(int goal, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp) {
        editor.putInt("goal_current:", goal);
        editor.apply();
    }

    // if today not in db, use current goal
    public int getGoalByDayStr(String day, SavedDataOperatorInt callback) {
        return sp.getInt("goal:"+day, getCurrentGoal(null));
    }
    public void setGoalByDayStr(String day, int goal, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp) {
        editor.putInt("goal:"+day, goal);
        editor.apply();
    }

    public void clearData() {
        editor.clear();
        editor.apply();
    }

    // note: if not exist, will use default
    // might need to change in the future
    public IStatistics getStatByDayStr(String day, SavedDataOperatorIStat callback) {
        int totalSteps = this.getStepsByDayStr(day, null);
        int goal = this.getStepsByDayStr(day, null);

        int intentionalSteps = this.getIntentionalStepsByDayStr(day, null);
        Float MPH = this.getAvgMPHByDayStr(day, null);
        Long timeWalked = this.getExerciseTimeByDayStr(day, null);

        return new DailyStat(goal,totalSteps,intentionalSteps,timeWalked,MPH);
    }

    public boolean setStatByDayStr(String day, IStatistics stat) {
        this.setStepsByDayStr(day, stat.getTotalSteps(), null, null);
        this.setGoalByDayStr(day, stat.getGoal(), null, null);
        this.setIntentionalStepsByDayStr(day, stat.getIntentionalSteps(), null, null);
        this.setAvgMPHByDayStr(day, stat.getAverageMPH(), null, null);
        this.setExerciseTimeByDayStr(day, stat.getTimeWalked(), null, null);

        return true;
    }

    public List<IStatistics> getLastWeekSteps(String day, SavedDataOperatorListIStat callback) {
        List<String> days = ITimer.getLastWeekDayStrings(day);
        ArrayList<IStatistics> toReturn = new ArrayList<>();

        for(String d : days) {
            toReturn.add(this.getStatByDayStr(d, null));
        }

        return toReturn;
    }

    public List<IStatistics> getLastMonthStat(String day, SavedDataOperatorListIStat callback) {
        List<String> days = ITimer.getLastMonthDayStrings(day);
        ArrayList<IStatistics> toReturn = new ArrayList<>();

        for(String d : days) {
            toReturn.add(this.getStatByDayStr(d, null));
        }

        return toReturn;
    }

    // shared preference will NEVER know about the friends
    public List<IStatistics> getFriendMonthlyStat(String email, String day, SavedDataOperatorListIStat callback) {
        return null;
    }

    public boolean isFirstTimeUser() {
        return (sp.getBoolean("is_first_time_user", true));
    }
    public void setFirstTimeUser(boolean isFirstTime) {
        editor.putBoolean("is_first_time_user", false);
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
