package com.android.personbest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.personbest.SavedDataManager.SavedDataManager;
import com.android.personbest.SavedDataManager.SavedDataManagerSharedPreference;
import com.android.personbest.StepCounter.*;
import com.android.personbest.Timer.ITimer;
import com.android.personbest.Timer.TimerMock;
import com.android.personbest.Timer.TimerSystem;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MainActivity extends AppCompatActivity implements Observer {

    private static final int GOAL_INIT = 5000; // default
    private static final int STEP_INIT = 0;
    private static final long MILLISECONDS_IN_A_MINUTE = 60000;
    private static final long MILLISECONDS_IN_A_SECOND = 1000;

    // Const static member
    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";
    private static final String TAG = "MainActivity";
    private static final int DATE_STRING_LENGTH = 10;

    // private variables
    private String fitnessServiceKey;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private int goalNum;
    private boolean plannedExercise = false;
    private long timer;
    private int plannedSteps;
    private int totalIntentionalSteps;
    private StepCounterGoogleFit stepCounter;
    private IntentionalWalkUtils intentionalWalkUtils = new IntentionalWalkUtils();
    private SavedDataManager sd;
    private ITimer theTimer;
    private ProgressEncouragement progressEncouragement;
    private IDate theDate;
    private String today;
    private Integer todayInt;

    private boolean NDEBUG = true;

    // UI-related members
    private TextView stepsTodayVal;
    private TextView goalVal;
    private TextView stepsLeftVal;
    private TextView columns;
    private TextView plannedTimeValue;
    private TextView plannedStepValue;
    private TextView plannedMPHValue;
    private ProgressBar progressBar;


    public void update(Observable o, Object arg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stepCounter.updateStepCount();
                int totalSoFar = Integer.parseInt(stepsTodayVal.getText().toString());
                editor.putInt(String.valueOf(theDate.getDay()) + "_TotalSteps", totalSoFar);
                editor.apply();
                if(plannedTimeValue.getVisibility() == View.VISIBLE) {
                    long timeDiff = (System.currentTimeMillis() - timer);
                    plannedTimeValue.setText(String.valueOf(timeDiff / MILLISECONDS_IN_A_MINUTE));

                    int stepDiff = totalSoFar - plannedSteps;
                    plannedStepValue.setText(String.valueOf(stepDiff));

                    double currMph = intentionalWalkUtils.velocity(sp.getInt("Height", 0), stepDiff, timeDiff / MILLISECONDS_IN_A_SECOND);
                    plannedMPHValue.setText(String.valueOf(currMph));
                }
                int left = goalNum - totalSoFar;
                if(left > 0) stepsLeftVal.setText(String.valueOf(left));
                else stepsLeftVal.setText("0");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Activity self = this;

        // Setup UI
        stepsTodayVal = findViewById(R.id.stepsTodayVal);
        goalVal = findViewById(R.id.goalVal);
        stepsLeftVal = findViewById(R.id.stepsLeftVal);
        columns = findViewById(R.id.plannedColumns);
        plannedTimeValue = findViewById(R.id.timeValue);
        plannedStepValue = findViewById(R.id.stepValue);
        plannedMPHValue = findViewById(R.id.mphValue);
        setPlannedExerciseStatsVisibility(false);

        stepsTodayVal.setText(String.valueOf(STEP_INIT));
        stepsLeftVal.setText(String.valueOf(goalNum - STEP_INIT));

        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(GOAL_INIT);
        progressBar.setMin(0);
        progressBar.setProgress(0);

        sd = new SavedDataManagerSharedPreference(this);
        theTimer = new TimerSystem();
        progressEncouragement = new ProgressEncouragement(this);
        theDate = new DateCalendar();

        final Button startStopBtn = findViewById(R.id.startStop);
        startStopBtn.setText("  Start Walk/Run  ");
        startStopBtn.setTextColor(Color.BLACK);
        startStopBtn.setBackgroundColor(Color.GREEN);
        startStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plannedExercise = !plannedExercise;
                if(plannedExercise) {
                    startStopBtn.setText("    End Walk/Run    ");
                    startStopBtn.setBackgroundColor(Color.RED);
                    timer = System.currentTimeMillis();
                    plannedSteps = Integer.parseInt(stepsTodayVal.getText().toString());
                    setPlannedExerciseStatsVisibility(true);
                }
                else {
                    startStopBtn.setText("  Start Walk/Run  ");
                    startStopBtn.setBackgroundColor(Color.GREEN);
                    setPlannedExerciseStatsVisibility(false);
                    timer = System.currentTimeMillis() - timer;
                    plannedSteps = Integer.parseInt(stepsTodayVal.getText().toString()) - plannedSteps;
                    totalIntentionalSteps = sp.getInt(String.valueOf(theDate.getDay()) + "_IntentionalSteps", 0) + plannedSteps;
                    editor.putInt(String.valueOf(theDate.getDay()) + "_IntentionalSteps", totalIntentionalSteps);
                    editor.apply();
                    launchSummary(timer, plannedSteps);
                }
            }
        });

        // Check if this is the first time launching app
        sp = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        editor = sp.edit();
        if(sp.getAll().isEmpty()) {
            startActivity(new Intent(this, SetUpActivity.class));
            editor.putInt("Current Goal",GOAL_INIT);
            editor.apply();
        }

        goalNum = sp.getInt("Current Goal", GOAL_INIT);
        editor.putInt(String.valueOf(theDate.getDay()) + "_Goal", goalNum);
        goalVal.setText(String.valueOf(goalNum));
        progressBar.setMax(goalNum);
        stepsLeftVal.setText(String.valueOf(goalNum - STEP_INIT));

        // Set Up Google Fitness
        fitnessServiceKey = FITNESS_SERVICE_KEY;
        StepCounterFactory.put(fitnessServiceKey, new StepCounterFactory.BluePrint() {
            @Override
            public StepCounter create(MainActivity activity) {
                return new StepCounterGoogleFit(activity);
            }
        });

        fitnessServiceKey = getIntent().getStringExtra(FITNESS_SERVICE_KEY);
        if (fitnessServiceKey == null ){
            fitnessServiceKey = FITNESS_SERVICE_KEY;
        }
        stepCounter = (StepCounterGoogleFit) StepCounterFactory.create( fitnessServiceKey, this);
        stepCounter.setup();
        stepCounter.addObserver(this);
        stepCounter.beginUpdates();

        // Update Button
        Button btnUpdateSteps = findViewById(R.id.btnUpdateSteps);
        btnUpdateSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepCounter.updateStepCount();
            }
        });

        setToday(theTimer.getTodayString());
        todayInt = theDate.getDay();

        // yesterday
        // only once since data of yesterday never changes today
        if(!sd.isCheckedYesterdayGoal(today)) {
           sd.setCheckedYesterdayGoal(today);
            checkYesterdayGoalReach();
        }
        // not show sub-goal if goal met yesterday
        if(!sd.isShownYesterdayGoal(today) && !sd.isCheckedYesterdaySubGoal(today)) {
            sd.setCheckedYesterdaySubGoal(today);
            checkYesterdaySubGoalReach();
        }

        if(theTimer.isLateToday()) {
            checkSubGoalReach();
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        if(theTimer.isLateToday()) {
            checkSubGoalReach();
        }
        // update date
        if(todayInt != theDate.getDay()) {
            todayInt = theDate.getDay();
            setToday(theTimer.getTodayString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//       If authentication was required during google fit setup, this will be called after the user authenticates
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == stepCounter.getRequestCode()) {
                stepCounter.updateStepCount();
            }
        } else {
            Log.e(TAG, "ERROR, google fit result code: " + resultCode);
        }

        // update goals
        goalNum = sp.getInt("Current Goal",goalNum);
        setGoal(goalNum);
    }

    // goal is reached but should we display the message?
    public void goalReach() {
        if(!sd.isShownGoal(today)) {
            sd.setShownGoal(today);
            Log.i(TAG, "Show goal on: " + today);
            goalReached(false);
        }
    }

    // has prompted sub goal?
    // has prompted goal???
    // has made progress?
    public void checkSubGoalReach() {
        int todaySteps = Integer.parseInt(stepsTodayVal.getText().toString());
        int yesterdaySteps = sd.getYesterdaySteps(todayInt);

        if(!sd.isShownSubGoal(today) &&
                !sd.isShownGoal(today) &&
                todaySteps < goalNum &&
                progressEncouragement.progressMade(todaySteps, yesterdaySteps)) {
            Log.i(TAG, "Show sub goal on: " + today);
            sd.setShownSubGoal(today);
            progressEncouragement.showEncouragementMessage(todaySteps,yesterdaySteps);
        }
    }

    // check if goal reached yesterday
    // need to be called only once per day
    protected void checkYesterdayGoalReach() {
        String yesterday = theTimer.getYesterdayString();

        int yesterdaySteps = sd.getStepsDaysBefore(todayInt, 1);
        int yesterdayGoal = sd.getGoalDaysBefore(todayInt, 1);

        if(!sd.isShownYesterdayGoal(today) &&
                !sd.isShownGoal(yesterday) &&
                yesterdayGoal <= yesterdaySteps) {
            Log.i(TAG, "Show yesterday goal on: " + today);
            sd.setShownYesterdayGoal(today);
            goalReached(true);
        }
    }

    // check if sub goal reached yesterday
    // need to be called only once per day
    protected void checkYesterdaySubGoalReach() {
        String yesterday = theTimer.getYesterdayString();

        int yesterdayGoal = sd.getYesterdayGoal(todayInt);
        int yesterdaySteps = sd.getYesterdaySteps(todayInt);
        int dayBeforeYesterdaySteps = sd.getStepsDaysBefore(todayInt, 2);

        if(!sd.isShownYesterdaySubGoal(today) &&
                !sd.isShownSubGoal(yesterday) &&
                !sd.isShownYesterdayGoal(today) &&
                 yesterdaySteps < yesterdayGoal &&
                progressEncouragement.progressMade(yesterdaySteps,dayBeforeYesterdaySteps)) {
            Log.i(TAG, "Show yesterday sub goal on: " + today);
            sd.setShownYesterdaySubGoal(today);
            progressEncouragement.showEncouragementMessage(yesterdaySteps,dayBeforeYesterdaySteps);
        }
    }

    protected void goalReached(boolean isYesterday) {
        Log.d(TAG,"Goal reached");
        Log.d(TAG,"Reached goal is yesterday's? " + isYesterday);
        String title = "You Reached the Goal";
        if(isYesterday) title += " Yesterday";
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle(title)
                .setMessage("Congratulations! Do you want to set a new step goal?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       launchSetGoalActivity();
                    }
                })
                .setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    public void setStepCount(int stepCount) {
        stepsTodayVal.setText(String.valueOf(stepCount));
        int stepsToGoal = (stepCount <= goalNum) ? goalNum - stepCount: 0;
        stepsLeftVal.setText(String.valueOf(stepsToGoal));
        progressBar.setProgress(stepCount);
        if(stepsToGoal == 0) goalReach();
    }


    public void setGoal(int goalNum) {
        this.goalNum = goalNum;
        editor.putInt(String.valueOf(theDate.getDay()) + "_Goal", goalNum);
        editor.apply();
        this.goalVal.setText(String.valueOf(goalNum));

        // changing goal will also change progress
        int stepCount = Integer.valueOf(this.stepsTodayVal.getText().toString());
        int stepsToGoal = (stepCount <= goalNum) ? goalNum - stepCount: 0;
        stepsLeftVal.setText(String.valueOf(stepsToGoal));
        progressBar.setMax(goalNum);
        progressBar.setProgress(stepCount);
    }

    public int getGoal() {
        return this.goalNum;
    }

    // MM/dd/yyyy
    public void setToday(String today) {
        try {
            if(today.length() != DATE_STRING_LENGTH) {
                throw new IllegalArgumentException("Wrong date format");
            }
            int mm = Integer.parseInt(today.substring(0,2));
            int dd = Integer.parseInt(today.substring(3,5));
            int yyyy = Integer.parseInt(today.substring(6,DATE_STRING_LENGTH));
            if(! (1 <= mm && mm <= 12 && 1 <= dd && dd <= 31 && 0 < yyyy) ) {
                throw new IllegalArgumentException("Wrong date format");
            }
        } catch (IllegalFormatException e) {
            e.printStackTrace();
            throw e;
        }
        this.today = today;
        // not updating todayInt here to make mocking work
    }


    // for test
    public void setTimer(ITimer t) {
        this.theTimer = t;
        setToday(theTimer.getTodayString());
    }

    public void setSavedDataManager(SavedDataManager sd) {
        this.sd = sd;
    }

    public void setTheDate(IDate d) {
        this.theDate = d;
        todayInt = theDate.getDay();
    }

    public void setPlannedExerciseStatsVisibility(boolean visible) {
        if(visible) {
            columns.setVisibility(View.VISIBLE);
            plannedTimeValue.setVisibility(View.VISIBLE);
            plannedStepValue.setVisibility(View.VISIBLE);
            plannedMPHValue.setVisibility(View.VISIBLE);
        }
        else {
            columns.setVisibility(View.INVISIBLE);
            plannedTimeValue.setVisibility(View.INVISIBLE);
            plannedStepValue.setVisibility(View.INVISIBLE);
            plannedMPHValue.setVisibility(View.INVISIBLE);
        }
    }

    public StepCounter getStepCounter(){
        return this.stepCounter;
    }

    // for test
    public void setFitnessServiceKey(String fitnessServiceKey) {
        this.fitnessServiceKey = fitnessServiceKey;
    }



    public void launchProgressChart(View view) {
        Intent intent = new Intent(this, ProgressChart.class);
        startActivity(intent);
    }

    public void launchSummary(long timeElapsed, int stepsTaken) {
        Intent intent = new Intent(this, PlannedExerciseSummary.class);
        intent.putExtra("timeElapsed", timeElapsed);
        intent.putExtra("stepsTaken", stepsTaken);
        startActivity(intent);
    }

    public void launchSetGoalActivity(View view) {
        Intent intent = new Intent(this, SetGoalActivity.class);
        startActivityForResult(intent,0);
    }

    public void launchSetGoalActivity() {
        Intent intent = new Intent(this, SetGoalActivity.class);
        startActivity(intent);
    }
}