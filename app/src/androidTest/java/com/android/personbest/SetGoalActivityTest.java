package com.android.personbest;

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
    public ActivityTestRule<SetGoalActivity> mainActivityTestRule = new ActivityTestRule<SetGoalActivity>(SetGoalActivity.class);

    private SetGoalActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mainActivityTestRule.getActivity();
    }
    @Test
    public void save() {
        final long input = 777L;
        long outpout = 0L;
        SharedPreferences sharedPreferences = mActivity.getApplicationContext().getSharedPreferences("user_goal", MODE_PRIVATE);

        mActivity.save(777L);
        outpout = sharedPreferences.getLong("stepNumber", 0L);
        assertEquals(input, outpout);

    }
}