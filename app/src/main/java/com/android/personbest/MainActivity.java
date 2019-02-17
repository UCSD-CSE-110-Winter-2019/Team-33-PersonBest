package com.android.personbest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
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

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer {

    private static final int GOAL_INIT = 5000; // default
    private static final int STEP_INIT = 0;
    private static final long MILLISECONDS_IN_A_MINUTE = 60000;
    private static final long MILLISECONDS_IN_A_SECOND = 1000;

    // Const static member
    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";
    private static final String TAG = "MainActivity";

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
    private SavedDataManager savedDataManager;
    private int today;

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
                editor.putInt(String.valueOf(Calendar.DAY_OF_WEEK) + "_TotalSteps", totalSoFar);
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
    protected void onStart() {
        super.onStart();
        Log.d("State", "OnStart");
        //initGoal();
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

        savedDataManager = new SavedDataManagerSharedPreference(this);

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
                    totalIntentionalSteps = sp.getInt(String.valueOf(Calendar.DAY_OF_WEEK) + "_IntentionalSteps", 0) + plannedSteps;
                    editor.putInt(String.valueOf(Calendar.DAY_OF_WEEK) + "_IntentionalSteps", totalIntentionalSteps);
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
        }

        goalNum = sp.getInt("Current Goal", 5000);
        editor.putInt(String.valueOf(Calendar.DAY_OF_WEEK) + "_Goal", goalNum);
        goalVal.setText(String.valueOf(goalNum));
        progressBar.setMax(goalNum);
        stepsLeftVal.setText(String.valueOf(goalNum - STEP_INIT));

        // Set Up Google Fitness
        fitnessServiceKey = FITNESS_SERVICE_KEY;
        StepCounterFactory.put(fitnessServiceKey, new StepCounterFactory.BluePrint() {
            @Override
            public StepCounter create(MainActivity activity) {
                System.err.println("Main Activity: " + String.valueOf(activity));
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

        today = ZonedDateTime.now(ZoneId.systemDefault()).getDayOfWeek().getValue() - 1;
        checkYesterdayGoalReach();
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
    }

    protected void checkYesterdayGoalReach() {
        int lastDayPromptedYesterdayGoal = sp.getInt("last_day_prompted_yesterday_goal",today);;

        int yesterdayGoal = savedDataManager.getYesterdayGoal(today);
        int yesterdaySteps = savedDataManager.getYesterdaySteps(today);

        if(lastDayPromptedYesterdayGoal != today && yesterdayGoal <= yesterdaySteps) {
            goalReached(true);
            editor.putInt("last_day_prompted_yesterday_goal",today);
            editor.apply();
        }
    }

    public void goalReached(boolean isYesterday) {
        String title = "You Reached the Goal";
        if(isYesterday) title += " Yesterday";
        Context context = getApplicationContext();
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle(title)
                .setMessage("Congratulations! Do you want to set a new step goal?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       launchSetGoalActivity();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void setStepCount(int stepCount) {
        stepsTodayVal.setText(String.valueOf(stepCount));
        int stepsToGoal = (stepCount <= goalNum) ? goalNum - stepCount: 0;
        stepsLeftVal.setText(String.valueOf(stepsToGoal));
        progressBar.setProgress(stepCount);
        if(stepsToGoal == 0) {
            int lastDayPromptedGoal = sp.getInt("last_day_prompted_goal",today);
            if(lastDayPromptedGoal != today) {
                this.goalReached(false);
                editor.putInt("last_day_prompted_goal",today);
                editor.apply();
            }
        }
    }


    public void setGoal(int goalNum) {
        this.goalNum = goalNum;
        editor.putInt(String.valueOf(Calendar.DAY_OF_WEEK) + "_Goal", goalNum);
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

    public void setToday(int date) {
        if(date < 0 || date >= 7) this.today = date;
        else throw new IllegalArgumentException("Wrong date format");
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
        startActivity(intent);
    }

    public void launchSetGoalActivity() {
        Intent intent = new Intent(this, SetGoalActivity.class);
        startActivity(intent);
    }
}