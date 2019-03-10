package com.android.personbest;

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

public class PlannedExerciseSummary extends AppCompatActivity {

    public static final long MILLISECONDS_IN_A_MINUTE = 60000;
    public static final long MILLISECONDS_IN_A_SECOND = 1000;
    private static ExecMode.EMode test_mode;
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

        // we testing?
        test_mode = ExecMode.getExecMode();
        if(test_mode == ExecMode.EMode.TEST_CLOUD) {
            sd = new SavedDataManagerSharedPreference(this); // TODO a mock firestore adapter
        } else if (test_mode == ExecMode.EMode.TEST_LOCAL) {
            sd = new SavedDataManagerSharedPreference(this);
        }
        else {
            // set saved data manager
            sd = new SavedDataManagerFirestore(this);
        }

        theTimer = new TimerSystem();

        displaySteps = findViewById(R.id.stepsTaken);
        displayTime = findViewById(R.id.timeElapsed);
        displayMph = findViewById(R.id.mph);

        Intent intent = getIntent();
        int stepsTaken = intent.getIntExtra("stepsTaken", 0);
        long msElapsed = intent.getLongExtra("timeElapsed", 0);

        if(test_mode == ExecMode.EMode.DEFAULT) {
            sd.getExerciseTimeByDayStr(theTimer.getTodayString(), totalTime -> {
                sd.getIntentionalStepsByDayStr(theTimer.getTodayString(), totalSteps -> {
                    double meanMPH = intentionalWalkUtils.velocity(sd.getUserHeight(null), totalSteps,
                            (totalTime + msElapsed) / MILLISECONDS_IN_A_SECOND);
                    sd.setAvgMPHByDayStr(theTimer.getTodayString(), (float) meanMPH, null, null);
                });

            });
        }
        else {
            long totalTime = msElapsed + sd.getExerciseTimeByDayStr(theTimer.getTodayString(), null);
            int totalSteps = sd.getIntentionalStepsByDayStr(theTimer.getTodayString(), null);
            double meanMPH = intentionalWalkUtils.velocity(sd.getUserHeight(null), totalSteps, totalTime / MILLISECONDS_IN_A_SECOND);
            sd.setAvgMPHByDayStr(theTimer.getTodayString(), (float) meanMPH, null, null);
        }

        displaySteps.setText("Steps Taken: " + stepsTaken);
        displayTime.setText("Minutes Elapsed: " + (msElapsed / MILLISECONDS_IN_A_MINUTE));
        displayMph.setText("MPH: " + intentionalWalkUtils.velocity(sd.getUserHeight(null), stepsTaken, msElapsed / MILLISECONDS_IN_A_SECOND));

        button = findViewById(R.id.goBack);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}