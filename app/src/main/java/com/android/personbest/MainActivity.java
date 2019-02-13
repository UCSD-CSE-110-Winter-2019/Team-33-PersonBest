package com.android.personbest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.widget.Toast;
import com.android.personbest.StepCounter.*;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer {

    // FIXME hardcoded goal
    private static final int GOAL_INIT = 1000;
    private static final int STEP_INIT = 0;
    private static final long UPDATE_INTERVAL = 1000;
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
    private StepCounterGoogleFit stepCounter;
    private IntentionalWalkUtils intentionalWalkUtils = new IntentionalWalkUtils();

    // UI-related members
    private TextView stepsTodayVal;
    private TextView goalVal;
    private TextView stepsLeftVal;
    private TextView columns;
    private TextView plannedTimeValue;
    private TextView plannedStepValue;
    private TextView plannedMPHValue;
    private ProgressBar progressBar;


    public void update(Observable o, Object arg){
        stepCounter.updateStepCount();
        if(plannedTimeValue.getVisibility() == View.VISIBLE) {
            long timeDiff = (System.currentTimeMillis() - timer);
            plannedTimeValue.setText(String.valueOf(timeDiff / MILLISECONDS_IN_A_MINUTE));

            int stepDiff = Integer.parseInt(stepsTodayVal.getText().toString()) - plannedSteps;
            plannedStepValue.setText(String.valueOf(stepDiff));

            double currMph = intentionalWalkUtils.velocity(sp.getInt("Height", 0), stepDiff, timeDiff / MILLISECONDS_IN_A_SECOND);
            plannedMPHValue.setText(String.valueOf(currMph));
        }
        stepsLeftVal.setText(String.valueOf(goalNum - Integer.parseInt(stepsTodayVal.getText().toString())));
    }

    /*private class StepUpdate extends AsyncTask<String, String, String> {
        private String resp = "";
        @Override
        protected void onPreExecute() {}

        @Override
        protected String doInBackground(String... params) {
            int loops = 2;
            try {
                while (loops >= 0) {
                    loops--;
                    Thread.sleep(UPDATE_INTERVAL);
                    publishProgress(resp);
                }
            }
            catch(Exception e) {

            }
            return resp;
        }

        @Override
        protected void onProgressUpdate(String... result) {
            stepCounter.updateStepCount();
            if(plannedTimeValue.getVisibility() == View.VISIBLE) {
                long timeDiff = (System.currentTimeMillis() - timer);
                plannedTimeValue.setText(String.valueOf(timeDiff / MILLISECONDS_IN_A_MINUTE));

                int stepDiff = Integer.parseInt(stepsTodayVal.getText().toString()) - plannedSteps;
                plannedStepValue.setText(String.valueOf(stepDiff));

                double currMph = intentionalWalkUtils.velocity(sp.getInt("Height", 0), stepDiff, timeDiff / MILLISECONDS_IN_A_SECOND);
                plannedMPHValue.setText(String.valueOf(currMph));
            }
            stepsLeftVal.setText(String.valueOf(goalNum - Integer.parseInt(stepsTodayVal.getText().toString())));
        }

        @Override
        protected void onPostExecute(String result) {}
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup UI
        stepsTodayVal = findViewById(R.id.stepsTodayVal);
        goalVal = findViewById(R.id.goalVal);
        stepsLeftVal = findViewById(R.id.stepsLeftVal);
        columns = findViewById(R.id.plannedColumns);
        plannedTimeValue = findViewById(R.id.timeValue);
        plannedStepValue = findViewById(R.id.stepValue);
        plannedMPHValue = findViewById(R.id.mphValue);
        setPlannedExerciseStatsVisibility(false);

        progressBar = findViewById(R.id.progressBar);
        stepsTodayVal.setText(String.valueOf(STEP_INIT));
        stepsLeftVal.setText(String.valueOf(goalNum - STEP_INIT));
        progressBar.setMax(goalNum);
        progressBar.setMin(0);
        progressBar.setProgress(0);
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
                    launchSummary(timer, plannedSteps);
                }
            }
        });

        // Check if this is the first time launching app
        sp = getPreferences(Context.MODE_PRIVATE);
        editor = sp.edit();
        if(sp.getAll().isEmpty()) {
            startActivity(new Intent(this, SetUpActivity.class));
        }


        goalNum = sp.getInt("Current Goal", 5000);
        goalVal.setText(String.valueOf(goalNum));
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
        //System.out.println("Debug");
        if (fitnessServiceKey == null ){
            fitnessServiceKey = FITNESS_SERVICE_KEY;
        }
        stepCounter = (StepCounterGoogleFit) StepCounterFactory.create( fitnessServiceKey, this);
        stepCounter.setup();
        stepCounter.addObserver(this);

        Button btnUpdateSteps = findViewById(R.id.btnUpdateSteps);
        btnUpdateSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepCounter.updateStepCount();
            }
        });

        // Run Async Task on UI Thread
        // StepUpdate runner = new StepUpdate();
        // runner.execute(startStopBtn.getText().toString());
    }

    public void launchSummary(long timeElapsed, int stepsTaken) {
        Intent intent = new Intent(this, PlannedExerciseSummary.class);
        intent.putExtra("timeElapsed", timeElapsed);
        intent.putExtra("stepsTaken", stepsTaken);
        startActivity(intent);
    }

    // for test
    public void setFitnessServiceKey(String fitnessServiceKey) {
        this.fitnessServiceKey = fitnessServiceKey;
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

    public void setStepCount(int stepCount) {
        stepsTodayVal.setText(String.valueOf(stepCount));
        int stepsToGoal = (stepCount <= goalNum) ? goalNum - stepCount: 0;
        stepsLeftVal.setText(String.valueOf(stepsToGoal));
        progressBar.setProgress(stepCount);
        //showEncouragement(stepCount);
    }

    public void setGoal(int goalNum) {
        this.goalNum = goalNum;
        this.goalVal.setText(String.valueOf(goalNum));

        // changing goal will also change progress
        int stepCount = Integer.valueOf(this.stepsTodayVal.getText().toString());
        int stepsToGoal = (stepCount <= goalNum) ? goalNum - stepCount: 0;
        stepsLeftVal.setText(String.valueOf(stepsToGoal));
        progressBar.setMax(goalNum);
        progressBar.setProgress(stepCount);
    }

    public void showEncouragement(int stepCount) {
        long percentage = (int)Math.floor(stepCount * 100.0 / goalNum);
        if(percentage < 10) return;

        Context context = getApplicationContext();
        CharSequence text = "Good job! You're already at " + percentage + "% of the daily recommended number of steps.";
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
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
}