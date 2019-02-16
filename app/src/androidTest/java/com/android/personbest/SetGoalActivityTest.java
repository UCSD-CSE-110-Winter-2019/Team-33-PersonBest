package com.android.personbest;

import android.content.Intent;
import com.android.personbest.StepCounter.StepCounter;
import com.android.personbest.StepCounter.StepCounterFactory;
import com.android.personbest.StepCounter.StepCounterGoogleFit;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

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


        SetGoalActivity.save();
    }
}