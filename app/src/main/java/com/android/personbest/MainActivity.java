package com.android.personbest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.widget.Toast;
import com.android.personbest.StepCounter.*;

public class MainActivity extends AppCompatActivity {
    private String fitnessServiceKey = "GOOGLE_FIT";

    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";

    private static final String TAG = "MainActivity";

    private static final int GOAL_INIT = 10000;

    private int goalNum = GOAL_INIT;

    private StepCounter stepCounter;

    private TextView stepsTodayVal;
    private TextView goalVal;
    private TextView stepsLeftVal;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stepsTodayVal = findViewById(R.id.stepsTodayVal);
        goalVal = findViewById(R.id.goalVal);
        stepsLeftVal = findViewById(R.id.stepsLeftVal);
        progressBar = findViewById(R.id.progressBar);

        goalVal.setText(String.valueOf(goalNum));
        progressBar.setMax(goalNum);
        progressBar.setMin(0);

        fitnessServiceKey = FITNESS_SERVICE_KEY;

        Button btnUpdateSteps = findViewById(R.id.btnUpdateSteps);
        btnUpdateSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepCounter.updateStepCount();
            }
        });


        StepCounterFactory.put(fitnessServiceKey, new StepCounterFactory.BluePrint() {
            @Override
            public StepCounter create(MainActivity activity) {
                System.err.println("Main Activity: " + String.valueOf(activity));
                return new StepCounterGoogleFit(activity);
            }
        });
        stepCounter = StepCounterFactory.create( fitnessServiceKey, this);

        stepCounter.setup();
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

    public void showEncouragement(int stepCount) {
        long percentage = (int)Math.floor(stepCount * 100.0 / goalNum);
        if(percentage < 10) return;

        Context context = getApplicationContext();
        CharSequence text = "Good job! You're already at " + percentage + "% of the daily recommended number of steps.";
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }
}