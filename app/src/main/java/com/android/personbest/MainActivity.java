package com.android.personbest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    // FIXME hardcoded goal
    private static final int GOAL_INIT = 10000;
    private static final int STEP_INIT = 0;

    // Const static member
    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";
    private static final String TAG = "MainActivity";

    private String fitnessServiceKey;
    private int goalNum = GOAL_INIT;
    private boolean plannedExercise = false;
    private long timer;
    private int plannedSteps;
    private StepCounter stepCounter;

    // UI-related members
    private TextView stepsTodayVal;
    private TextView goalVal;
    private TextView stepsLeftVal;
    private ProgressBar progressBar;

    private class StepUpdate extends AsyncTask<String, String, String> {
        private String resp = "";

        @Override
        protected void onPreExecute() {}

        @Override
        protected String doInBackground(String... params) {
            publishProgress(resp);
            return resp;
        }

        @Override
        protected void onProgressUpdate(String... result) { stepCounter.updateStepCount(); }

        @Override
        protected void onPostExecute(String result) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup UI
        stepsTodayVal = findViewById(R.id.stepsTodayVal);
        goalVal = findViewById(R.id.goalVal);
        stepsLeftVal = findViewById(R.id.stepsLeftVal);
        progressBar = findViewById(R.id.progressBar);
        stepsTodayVal.setText(String.valueOf(STEP_INIT));
        goalVal.setText(String.valueOf(goalNum));
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
                if(plannedExercise) {
                    startStopBtn.setText("    End Walk/Run    ");
                    startStopBtn.setBackgroundColor(Color.RED);
                    timer = System.currentTimeMillis();
                    plannedSteps = Integer.parseInt(stepsTodayVal.getText().toString());
                }
                else {
                    startStopBtn.setText("  Start Walk/Run  ");
                    startStopBtn.setBackgroundColor(Color.GREEN);
                    timer = System.currentTimeMillis() - timer;
                    plannedSteps = Integer.parseInt(stepsTodayVal.getText().toString()) - plannedSteps;
                    launchSummary(timer, plannedSteps);
                }
                plannedExercise = !plannedExercise;
            }
        });

        // Set Up Google Fitness
        fitnessServiceKey = FITNESS_SERVICE_KEY;
        StepCounterFactory.put(fitnessServiceKey, new StepCounterFactory.BluePrint() {
            @Override
            public StepCounter create(MainActivity activity) {
                System.err.println("Main Activity: " + String.valueOf(activity));
                return new StepCounterGoogleFit(activity);
            }
        });
        stepCounter = StepCounterFactory.create( fitnessServiceKey, this);
        stepCounter.setup();

        // Run Async Task on UI Thread
        StepUpdate runner = new StepUpdate();
        runner.execute(startStopBtn.getText().toString());
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
        showEncouragement(stepCount);
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
}