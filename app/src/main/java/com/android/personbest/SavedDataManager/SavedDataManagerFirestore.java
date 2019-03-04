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
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SavedDataManagerFirestore implements SavedDataManager {
    private static final int GOAL_INIT = 0;
    private final int DEFAULT_STEPS = 0;
    private final int DEFAULT_GOAL = 5000;
    private final float DEFAULT_MPH = 0;
    private final Long DEFAULT_TIME = 0L;
    private final String TAG = "SavedDataManagerFirestore";

    private Activity activity;
    private SavedDataManagerSharedPreference sdsp;
    private FirebaseFirestore ff;

    public SavedDataManagerFirestore(Activity activity) {
        this.activity = activity;
        this.sdsp = new SavedDataManagerSharedPreference(activity);
        this.ff = FirebaseFirestore.getInstance();
    }

    public String getIdByEmail(String email) {
        return null;
    }

    public int getYesterdaySteps(int day){
        return 0;
    }


    public int getStepsDaysBefore(int today, int days) {
        return 0;
    }

    public int getYesterdayGoal(int day){
        return 0;
    }

    public int getGoalDaysBefore(int today, int days) {
        return 0;
    }

    public List<IStatistics> getLastWeekSteps(int day){
        return null;
//        SharedPreferences sp = activity.getSharedPreferences("user_data",Context.MODE_PRIVATE);
//        List<IStatistics> result = new ArrayList<>();
//        for (Integer d = 1; d <= day; d++){
//            int totalSteps = sp.getInt(d.toString() + "_TotalSteps",DEFAULT_STEPS);
//            int intentionalSteps = sp.getInt(d.toString()+"_IntentionalSteps",DEFAULT_STEPS);
//            int goal = sp.getInt(d.toString()+"_Goal",DEFAULT_GOAL);
//            Float MPH = sp.getFloat(d.toString()+"_AverageMPH",DEFAULT_MPH);
//            Long timewalked = sp.getLong(d.toString()+"_ExerciseTime",DEFAULT_TIME);
//            DailyStat dailyStat = new DailyStat(goal,totalSteps,intentionalSteps,timewalked,MPH);
//            result.add(dailyStat);
//        }
//
//        return result;

    }

    public boolean isFirstTimeUser() {
        return sdsp.isFirstTimeUser();
    }
    public void setFirstTimeUser(boolean isFirstTime) {
        sdsp.setFirstTimeUser(isFirstTime);
    }

    // data to sync

    public int getUserHeight() {
        return 0;
    }
    public boolean setUserHeight(int height) {
        return false;
    }

    public int getStepsByDayStr(String day) {
        return 0;
    }
    public boolean setStepsByDayStr(String day, int step) {
        return false;
    }

    public int getIntentionalStepsByDayStr(String day) {
        return 0;
    }
    public boolean setIntentionalStepsByDayStr(String day, int step) {
        return false;
    }

    public long getExerciseTimeByDayStr(String day) {
        return 0;
    }

    public boolean setExerciseTimeByDayStr(String day, long time) {
        return false;
    }

    public float getAvgMPHByDayStr(String day) {
        return 0;
    }
    public boolean setAvgMPHByDayStr(String day, float mph) {
        return false;
    }

    public int getCurrentGoal() {
        return 0;
    }
    public boolean setCurrentGoal(int goal) {
        return false;
    }

    // if today not in db, use current goal
    public int getGoalByDayStr(String day) {
        // if today not in db, use current goal
        return 0;
    }
    public boolean setGoalByDayStr(String day, int goal) {
        return false;
    }

    // clear everything
    public void clearData() {

    }

    // note: if not exist, will use default
    // might need to change in the future
    public IStatistics getStatByDayStr(String day) {
        return null;
//        int totalSteps = this.getStepsByDayStr(day);
//        int goal = this.getStepsByDayStr(day);
//
//        int intentionalSteps =
//        Float MPH =
//        Long timeWalked =
//
//        return new DailyStat(goal,totalSteps,intentionalSteps,timeWalked,MPH);
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
        return null;
    }

    ////////////////////////////////////////////////////////////////////////

    public boolean isShownGoal(String today) {
        return sdsp.isShownGoal(today);
    }
    public void setShownGoal(String today){
        sdsp.setShownGoal(today);
    }

    public boolean isShownSubGoal(String today){
        return sdsp.isShownSubGoal(today);
    }
    public void setShownSubGoal(String today){
        sdsp.setShownSubGoal(today);
    }

    public boolean isCheckedYesterdayGoal(String today) {
        return sdsp.isCheckedYesterdayGoal(today);
    }

    public void setCheckedYesterdayGoal(String today) {
        sdsp.setCheckedYesterdayGoal(today);
    }

    public boolean isShownYesterdayGoal(String today) {
        return sdsp.isShownYesterdayGoal(today);
    }
    public void setShownYesterdayGoal(String today) {
        sdsp.setShownYesterdayGoal(today);
    }

    public boolean isCheckedYesterdaySubGoal(String today) {
        return sdsp.isCheckedYesterdaySubGoal(today);
    }

    public void setCheckedYesterdaySubGoal(String today) {
        sdsp.setCheckedYesterdaySubGoal(today);
    }

    public boolean isShownYesterdaySubGoal(String today) {
        return sdsp.isShownYesterdaySubGoal(today);
    }
    public void setShownYesterdaySubGoal(String today) {
        sdsp.setShownYesterdaySubGoal(today);
    }
}
