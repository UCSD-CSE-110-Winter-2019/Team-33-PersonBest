package com.android.personbest;

import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;

import com.android.personbest.SavedDataManager.SavedDataManager;
import com.android.personbest.SavedDataManager.SavedDataManagerSharedPreference;
import com.android.personbest.StepCounter.StepCounter;
import com.android.personbest.StepCounter.StepCounterFactory;
import com.android.personbest.StepCounter.StepCounterGoogleFit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class TestSummary {
    Activity mainActivity;
    Activity activity;

    private static final String TEST_SERVICE = "TEST_SERVICE";
    private static final long TWO_MIN = 120000;
    private static final int STEPS = 150;

    private TextView displaySteps;
    private TextView displayTime;
    private TextView displayMph;
    private SavedDataManager sd;

    @Before
    public void setup() {
        StepCounterFactory.put(TEST_SERVICE, new StepCounterFactory.BluePrint() {
            @Override
            public StepCounter create(MainActivity stepCountActivity) {
                return new StepCounterGoogleFit(stepCountActivity);
            }
        });

        ExecMode.setExecMode(ExecMode.EMode.TEST_LOCAL);

        Intent intent = new Intent(RuntimeEnvironment.application, MainActivity.class);
        intent.putExtra(MainActivity.FITNESS_SERVICE_KEY, TEST_SERVICE);
        try {
            mainActivity = Robolectric.buildActivity(MainActivity.class, intent).create().get();
        } catch (Exception e) {
            mainActivity = Robolectric.buildActivity(MainActivity.class, intent).create().get();
        }
        sd = new SavedDataManagerSharedPreference(mainActivity);
        sd.setUserHeight(72, null, null);

        Intent summary = new Intent(mainActivity, PlannedExerciseSummary.class);
        summary.putExtra("timeElapsed", TWO_MIN);
        summary.putExtra("stepsTaken", STEPS);
        activity = Robolectric.buildActivity(PlannedExerciseSummary.class, summary).create().get();

        displaySteps = activity.findViewById(R.id.stepsTaken);
        displayTime = activity.findViewById(R.id.timeElapsed);
        displayMph = activity.findViewById(R.id.mph);
    }

    @Test
    public void testActivity() {
        assertEquals(displaySteps.getText().toString(), "Steps Taken: 150");
        assertEquals(displayTime.getText().toString(), "Minutes Elapsed: 2");
        assertEquals(displayMph.getText().toString(), "MPH: 2.1");
    }
}
