package com.android.personbest.SavedDataManager;

import com.android.personbest.StepCounter.IStatistics;

import java.util.List;

///////////////////////////////////////////////////
// README                                        //
// note for callers:                             //
// will pass null or -1 to callback when failure //
// get todayStr from `theTimer.getTodayString()` //
///////////////////////////////////////////////////
public interface SavedDataManager {
    // data only available in the cloud
    // pass a callback function to use the result
    void getIdByEmail(String email, SavedDataOperatorString callback);

    // data to sync

    // will call callback with -1 if failure
    int getUserHeight(SavedDataOperatorInt callback);
    // will call 2 operator with corresponding messages
    void setUserHeight(int height, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp);

    int getStepsByDayStr(String day, SavedDataOperatorInt callback);
    void setStepsByDayStr(String day, int step, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp);
    int getIntentionalStepsByDayStr(String day, SavedDataOperatorInt callback);
    void setIntentionalStepsByDayStr(String day, int step, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp);

    long getExerciseTimeByDayStr(String day, SavedDataOperatorLong callback);
    void setExerciseTimeByDayStr(String day, long time, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp);
    float getAvgMPHByDayStr(String day, SavedDataOperatorFloat callback);
    void setAvgMPHByDayStr(String day, float mph, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp);

    int getCurrentGoal(SavedDataOperatorInt callback);
    void setCurrentGoal(int goal, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp);
    int getGoalByDayStr(String day, SavedDataOperatorInt callback);
    void setGoalByDayStr(String day, int goal, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp);

    // clear everything
    // for test only
    void clearData();

    IStatistics getStatByDayStr(String day, SavedDataOperatorIStat callback);
    //boolean setStatByDayStr(String day, IStatistics stat); // unnecessary

    // return list of IStatistics
    List<IStatistics> getLastWeekSteps(String day, SavedDataOperatorListIStat callback);
    // return a list of IStatistics
    // 28 days before the day in the argument
    List<IStatistics> getLastMonthStat(String day, SavedDataOperatorListIStat callback);
    List<IStatistics> getFriendMonthlyStat(String email, SavedDataOperatorListIStat callback);

    //////////////////////////////////////////////////////////////////////////////

    // below data are local only

    boolean isFirstTimeUser();
    void setFirstTimeUser(boolean isFirstTime);

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
