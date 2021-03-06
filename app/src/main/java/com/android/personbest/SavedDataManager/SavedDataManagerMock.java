package com.android.personbest.SavedDataManager;

import com.android.personbest.StepCounter.IStatistics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SavedDataManagerMock implements SavedDataManager, Serializable {
    private List<IStatistics> data;
    private List<IStatistics> dataFriend;
    private String friendEmail;
    private String friendID;

    public SavedDataManagerMock(List<IStatistics> data, List<IStatistics> dataFriend, String friendEmail, String friendID) {
        this.data = data;
        this.dataFriend = dataFriend;
        this.friendEmail = friendEmail;
        this.friendID = friendID;
    }

    @Override
    public void getIdByEmail(String email, SavedDataOperatorString callback) {
        callback.op(friendID);
    }

    @Override
    public List<IStatistics> getLastWeekSteps(String day, SavedDataOperatorListIStat callback) {
        return data;
    }

    @Override
    public void clearData() { }

    @Override
    public List<IStatistics> getLastMonthStat(String day, SavedDataOperatorListIStat callback) {
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

    public int getStepsByDayStr(String day, SavedDataOperatorInt callback) {
        return -1;
    }
    public void setStepsByDayStr(String day, int step, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp) {
    }
    public int getGoalByDayStr(String day, SavedDataOperatorInt callback) {
        return -1;
    }
    public void setGoalByDayStr(String day, int goal, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp) {
    }
    public IStatistics getStatByDayStr(String day, SavedDataOperatorIStat callback) {
        return null;
    }
    public boolean setStatByDayStr(String day, IStatistics stat) {
        return true;
    }

    @Override
    public boolean isFirstTimeUser() {
        return false;
    }

    @Override
    public void setFirstTimeUser(boolean isFirstTime) {

    }

    @Override
    public void setUserHeight(int height, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp) {
    }

    @Override
    public int getUserHeight(SavedDataOperatorInt callback) {
        return 0;
    }

    @Override
    public void setExerciseTimeByDayStr(String day, long time, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp) {
    }

    @Override
    public long getExerciseTimeByDayStr(String day, SavedDataOperatorLong callback) {
        return 0;
    }

    @Override
    public void setIntentionalStepsByDayStr(String day, int step, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp) {
    }

    @Override
    public int getIntentionalStepsByDayStr(String day, SavedDataOperatorInt callback) {
        return 0;
    }

    @Override
    public void setAvgMPHByDayStr(String day, float mph, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp) {
    }

    @Override
    public float getAvgMPHByDayStr(String day, SavedDataOperatorFloat callback) {
        return 0;
    }

    public void setCurrentGoal(int goal, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp) { }
    public int getCurrentGoal(SavedDataOperatorInt callback) { return 0; }

    @Override
    public List<IStatistics> getFriendMonthlyStat(String email, String day, SavedDataOperatorListIStat callback) {
        return null;
    }

    /////////////////////////////////////////////////////////////////////

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
