package com.android.personbest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class TestDailyStats {
    private static final String TEST_SERVICE = "TEST_SERVICE";
    private MainActivity mainActivity;
    private static final int TEN_K = 10000;
    private static final int FIVE_K = 5000;

    @Before
    public void setup() {

        StepCounterFactory.put(TEST_SERVICE, new StepCounterFactory.BluePrint() {
            @Override
            public StepCounter create(MainActivity stepCountActivity) {
                return new StepCounterGoogleFit(stepCountActivity);
            }
        });

        Intent intent = new Intent(RuntimeEnvironment.application, MainActivity.class);
        intent.putExtra(MainActivity.FITNESS_SERVICE_KEY, TEST_SERVICE);
        //System.err.println(MainActivity.FITNESS_SERVICE_KEY);
        mainActivity = Robolectric.buildActivity(MainActivity.class, intent).create().get();
    }

    @Test
    public void testGetGoal() {
        SharedPreferences sp = mainActivity.getSharedPreferences("user_data", Context.MODE_PRIVATE);
        assertEquals(sp.getInt(String.valueOf(Calendar.DAY_OF_WEEK) + "_Goal", 0), 5000);
        assertEquals(sp.getInt(String.valueOf((Calendar.DAY_OF_WEEK + 1) % 7) + "_Goal", 0), 0);
    }

    @Test
    public void testSetGoal() {
        SharedPreferences sp = mainActivity.getSharedPreferences("user_data", Context.MODE_PRIVATE);
        assertEquals(sp.getInt(String.valueOf(Calendar.DAY_OF_WEEK) + "_Goal", 0), FIVE_K);
        mainActivity.setGoal(10000);
        assertEquals(sp.getInt(String.valueOf(Calendar.DAY_OF_WEEK) + "_Goal", 0), TEN_K);
    }
}
