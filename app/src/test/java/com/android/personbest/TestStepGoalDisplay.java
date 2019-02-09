package com.android.personbest;

import android.widget.ProgressBar;
import android.widget.TextView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class TestStepGoalDisplay {

    private MainActivity mainActivity;
    private TextView goalText;
    private TextView goalVal;

    private ProgressBar progressBar;

    @Before
    public void setup() {
        mainActivity = Robolectric.setupActivity(MainActivity.class);
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