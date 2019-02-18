package com.android.personbest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

public class PlannedExerciseSummary extends AppCompatActivity {

    public static final long MILLISECONDS_IN_A_MINUTE = 60000;
    public static final long MILLISECONDS_IN_A_SECOND = 1000;
    private TextView displaySteps;
    private TextView displayTime;
    private TextView displayMph;
    private Button button;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private IntentionalWalkUtils intentionalWalkUtils = new IntentionalWalkUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planned_exercise_summary);
        sp = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        editor = sp.edit();

        displaySteps = findViewById(R.id.stepsTaken);
        displayTime = findViewById(R.id.timeElapsed);
        displayMph = findViewById(R.id.mph);

        Intent intent = getIntent();
        int stepsTaken = intent.getIntExtra("stepsTaken", 0);
        long msElapsed = intent.getLongExtra("timeElapsed", 0);

        long totalTime = sp.getLong(String.valueOf(Calendar.DAY_OF_WEEK) + "_ExerciseTime", 0) + msElapsed;
        int totalSteps = sp.getInt(String.valueOf(Calendar.DAY_OF_WEEK) + "_IntentionalSteps", 0);
        double meanMPH = intentionalWalkUtils.velocity(sp.getInt("Height", 0), totalSteps, totalTime / MILLISECONDS_IN_A_SECOND);
        editor.putFloat(String.valueOf(Calendar.DAY_OF_WEEK) + "_AverageMPH", (float)meanMPH);
        editor.apply();

        displaySteps.setText("Steps Taken: " + stepsTaken);
        displayTime.setText("Minutes Elapsed: " + (msElapsed / MILLISECONDS_IN_A_MINUTE));
        displayMph.setText("MPH: " + intentionalWalkUtils.velocity(sp.getInt("Height", 0), stepsTaken, msElapsed / MILLISECONDS_IN_A_SECOND));

        button = findViewById(R.id.goBack);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}