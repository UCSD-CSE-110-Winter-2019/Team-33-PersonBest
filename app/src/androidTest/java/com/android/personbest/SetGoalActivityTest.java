package com.android.personbest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.test.rule.ActivityTestRule;
import android.widget.TextView;
import com.android.personbest.SavedDataManager.SavedDataManager;
import com.android.personbest.SavedDataManager.SavedDataManagerSharedPreference;
import com.android.personbest.StepCounter.StepCounter;
import com.android.personbest.StepCounter.StepCounterFactory;
import com.android.personbest.StepCounter.StepCounterGoogleFit;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.content.Context.MODE_PRIVATE;
import static org.junit.Assert.*;

public class SetGoalActivityTest {
    private static final boolean dummy = ExecMode.setExecMode(ExecMode.EMode.TEST_LOCAL);
    @Rule
    public ActivityTestRule<SetGoalActivity> setGoalActivityTestRule = new ActivityTestRule<SetGoalActivity>(SetGoalActivity.class);
    SavedDataManager sd;
    private SetGoalActivity setGoalActivity = null;

    @Before
    public void setUp() throws Exception {
        setGoalActivity = setGoalActivityTestRule.getActivity();
        sd = new SavedDataManagerSharedPreference(setGoalActivity);
    }
    @Test
    public void save() {
        final int input = 777;
        int output = 0;
        setGoalActivity.save(777);
        output = sd.getCurrentGoal();
        assertEquals(input, output);
    }

    @Test
    public void initGoal() {
        int goal1;
        int goal2;

        setGoalActivity.initGoal();
        goal1 = sd.getCurrentGoal() + 500;
        goal2 = setGoalActivity.getGoal();
        assertEquals(goal1, goal2);
    }
}