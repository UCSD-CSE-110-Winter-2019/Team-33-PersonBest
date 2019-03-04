package com.android.personbest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.android.personbest.SavedDataManager.SavedDataManager;
import com.android.personbest.SavedDataManager.SavedDataManagerFirestore;
import com.android.personbest.SavedDataManager.SavedDataManagerSharedPreference;
import com.android.personbest.Timer.ITimer;
import com.android.personbest.Timer.TimerSystem;

import java.util.Calendar;

public class PlannedExerciseSummary extends AppCompatActivity {

    public static final long MILLISECONDS_IN_A_MINUTE = 60000;
    public static final long MILLISECONDS_IN_A_SECOND = 1000;
    private TextView displaySteps;
    private TextView displayTime;
    private TextView displayMph;
    private Button button;
    private SavedDataManager sd;
    private SharedPreferences sp;
    private ITimer theTimer;
    private IntentionalWalkUtils intentionalWalkUtils = new IntentionalWalkUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planned_exercise_summary);

        sd = new SavedDataManagerFirestore(this);

        // we testing?
        ExecMode.EMode test_mode = ExecMode.getExecMode();
        if(test_mode == ExecMode.EMode.TEST_CLOUD) {
            sd = new SavedDataManagerSharedPreference(this); // TODO a mock firestore adapter
        } else if (test_mode == ExecMode.EMode.TEST_LOCAL) {
            sd = new SavedDataManagerSharedPreference(this);
        }

        theTimer = new TimerSystem();

        displaySteps = findViewById(R.id.stepsTaken);
        displayTime = findViewById(R.id.timeElapsed);
        displayMph = findViewById(R.id.mph);

        Intent intent = getIntent();
        int stepsTaken = intent.getIntExtra("stepsTaken", 0);
        long msElapsed = intent.getLongExtra("timeElapsed", 0);

        long totalTime = msElapsed + sd.getExerciseTimeByDayStr(theTimer.getTodayString());
        int totalSteps = sd.getIntentionalStepsByDayStr(theTimer.getTodayString());
        double meanMPH = intentionalWalkUtils.velocity(sd.getUserHeight(), totalSteps, totalTime / MILLISECONDS_IN_A_SECOND);
        sd.setAvgMPHByDayStr(theTimer.getTodayString(), (float)meanMPH);

        displaySteps.setText("Steps Taken: " + stepsTaken);
        displayTime.setText("Minutes Elapsed: " + (msElapsed / MILLISECONDS_IN_A_MINUTE));
        displayMph.setText("MPH: " + intentionalWalkUtils.velocity(sd.getUserHeight(), stepsTaken, msElapsed / MILLISECONDS_IN_A_SECOND));

        button = findViewById(R.id.goBack);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}