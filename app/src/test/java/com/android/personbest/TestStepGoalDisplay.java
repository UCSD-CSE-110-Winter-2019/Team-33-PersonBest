package com.android.personbest;

import android.content.Intent;
import android.widget.ProgressBar;
import android.widget.TextView;
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
public class TestStepGoalDisplay {

    private static final String TEST_SERVICE = "TEST_SERVICE";

    private MainActivity mainActivity;
    private TextView goalText;
    private TextView goalVal;

    private ProgressBar progressBar;

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
        mainActivity = Robolectric.buildActivity(MainActivity.class, intent).create().get();

        //mainActivity = Robolectric.setupActivity(MainActivity.class);
        goalText = mainActivity.findViewById(R.id.goalText);
        goalVal = mainActivity.findViewById(R.id.goalVal);
        progressBar = mainActivity.findViewById(R.id.progressBar);
    }

    @Test
    public void testGoalText() {
        assertEquals( "Goal:", goalText.getText().toString());
    }

    @Test
    public void testGoalValIsSet() {
        int testGoal = 2147483647;
        this.mainActivity.setGoal(testGoal);
        assertEquals(testGoal, progressBar.getMax());
        assertEquals(String.valueOf(testGoal), goalVal.getText().toString());
    }

    @Test
    public void testProgressbarProgress() {
        int testGoal = 2147483647;
        int testStepCount = 16384;
        this.mainActivity.setGoal(testGoal);
        this.mainActivity.setStepCount(testStepCount);
        assertEquals(testStepCount, progressBar.getProgress());
    }



}