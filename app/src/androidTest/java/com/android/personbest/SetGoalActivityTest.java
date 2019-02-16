package com.android.personbest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.test.rule.ActivityTestRule;
import android.widget.TextView;
import com.android.personbest.StepCounter.StepCounter;
import com.android.personbest.StepCounter.StepCounterFactory;
import com.android.personbest.StepCounter.StepCounterGoogleFit;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.content.Context.MODE_PRIVATE;
import static org.junit.Assert.*;

public class SetGoalActivityTest {
    @Rule
    public ActivityTestRule<SetGoalActivity> setGoalActivityTestRule = new ActivityTestRule<SetGoalActivity>(SetGoalActivity.class);

    private SetGoalActivity setGoalActivity = null;

    @Before
    public void setUp() throws Exception {
        setGoalActivity = setGoalActivityTestRule.getActivity();
    }
    @Test
    public void save() {
        final int input = 777;
        int output = 0;
        SharedPreferences sharedPreferences = setGoalActivity.getApplicationContext().getSharedPreferences("user_goal", MODE_PRIVATE);

        setGoalActivity.save(777);
        output = sharedPreferences.getInt("Current Goal", 0);
        assertEquals(input, output);
    }

}