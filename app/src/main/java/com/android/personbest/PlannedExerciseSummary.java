package com.android.personbest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PlannedExerciseSummary extends AppCompatActivity {

    public static final long MILLISECONDS_IN_A_MINUTE = 60000;
    public static final long STEPS_IN_A_MILE = 0;
    private TextView displaySteps;
    private TextView displayTime;
    private TextView displayMph;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planned_exercise_summary);

        displaySteps = findViewById(R.id.stepsTaken);
        displayTime = findViewById(R.id.timeElapsed);
        displayMph = findViewById(R.id.mph);

        Intent intent = getIntent();
        int stepsTaken = intent.getIntExtra("stepsTaken", 0);
        long timeElapsed = intent.getIntExtra("timeElapsed", 0);

        displaySteps.setText("Steps Taken: " + stepsTaken);
        displayTime.setText("Minutes Elapsed: " + (timeElapsed / MILLISECONDS_IN_A_MINUTE));
        displayMph.setText("MPH = 0");

        button = findViewById(R.id.goBack);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
