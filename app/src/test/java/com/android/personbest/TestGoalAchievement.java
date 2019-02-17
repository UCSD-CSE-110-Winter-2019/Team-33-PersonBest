package com.android.personbest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.android.personbest.SavedDataManager.SavedDataManager;
import com.android.personbest.SavedDataManager.SavedDataManagerSharedPreference;
import com.android.personbest.StepCounter.*;
import com.android.personbest.Timer.ITimer;
import com.android.personbest.Timer.TimerMock;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

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
import org.robolectric.shadows.ShadowAlertDialog;

import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.*;
import static org.robolectric.RuntimeEnvironment.application;

@RunWith(RobolectricTestRunner.class)
public class TestGoalAchievement {
    private static final String TEST_SERVICE = "TEST_SERVICE";
    private static final String TEST_DAY = "02/19/2019";
    private static final int TEST_DAY_INT = 3; // just random number
    private static final int TEST_DAY_HOUR = 19; // brfore 8 pm
    private static final String PAY_DAY = "02/01/2019";
    private static final int GOAL_INIT = 1024;
    private static final int HEIGHT = 40;
    private static final int NEXT_STEP_COUNT = 1337;

    private MainActivity activity;
    private SharedPreferences sp;
    private SavedDataManager sd;
    private SharedPreferences.Editor editor;
    private int nextStepCount;

    private IDate mockDate;
    private ITimer mockTimer;

    @Before
    public void setUp() throws Exception {
        StepCounterFactory.put(TEST_SERVICE, new StepCounterFactory.BluePrint() {
            @Override
            public StepCounter create(MainActivity stepCountActivity) {
                return new TestGoalAchievement.TestFitnessService(stepCountActivity);
            }
        });


        Intent intent = new Intent(application, MainActivity.class);
        intent.putExtra(MainActivity.FITNESS_SERVICE_KEY, TEST_SERVICE);
        activity = Robolectric.buildActivity(MainActivity.class, intent).create().get();
        //System.err.println(MainActivity.FITNESS_SERVICE_KEY);

        sd = new SavedDataManagerSharedPreference(activity);
        sp = activity.getSharedPreferences("user_data", Context.MODE_PRIVATE);
        mockDate = new DateCalendar(TEST_DAY_INT);
        mockTimer = new TimerMock(TEST_DAY_HOUR, TEST_DAY, PAY_DAY);

        activity.setTimer(mockTimer);
        activity.setTheDate(mockDate);

        editor = sp.edit();
        editor.putInt("Height", HEIGHT); // skip the set up screen
        editor.putInt("Current Goal", GOAL_INIT);
        editor.apply();
        nextStepCount = NEXT_STEP_COUNT;

    }

    @Test
    public void testTodayGoalReachedClean() {
        sd.setShownGoal(PAY_DAY);
        activity.setGoal(GOAL_INIT);
        activity.setStepCount(GOAL_INIT+1);
        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertNotNull(alertDialog);
        ShadowAlertDialog shadowAlertDialog = Shadows.shadowOf(alertDialog);
        assertEquals("You Reached the Goal",
                shadowAlertDialog.getTitle());
        assertEquals("Congratulations! Do you want to set a new step goal?",
                shadowAlertDialog.getMessage());
    }

    @Test
    public void testTodayGoalReachedPrompted() {
        sd.setShownGoal(TEST_DAY);
        activity.setGoal(GOAL_INIT);
        activity.setStepCount(GOAL_INIT+1);
        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertNull(alertDialog);
    }

    @Test
    public void testTodayGoalReachedPromptedSubGoal() {
        sd.setShownGoal(PAY_DAY);
        activity.setGoal(GOAL_INIT);
        activity.setStepCount(GOAL_INIT+1);
        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertNotNull(alertDialog);
        ShadowAlertDialog shadowAlertDialog = Shadows.shadowOf(alertDialog);
        assertEquals("You Reached the Goal",
                shadowAlertDialog.getTitle());
        assertEquals("Congratulations! Do you want to set a new step goal?",
                shadowAlertDialog.getMessage());
    }

    @Test
    public void testTodayGoalReachedPromptedYesterdayGoal() {
        sd.setShownGoal(PAY_DAY);
        sd.setShownSubGoal(TEST_DAY);
        sd.setShownYesterdayGoal(TEST_DAY);
        activity.setGoal(GOAL_INIT);
        activity.setStepCount(GOAL_INIT+1);
        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertNotNull(alertDialog);
        ShadowAlertDialog shadowAlertDialog = Shadows.shadowOf(alertDialog);
        assertEquals("You Reached the Goal",
                shadowAlertDialog.getTitle());
        assertEquals("Congratulations! Do you want to set a new step goal?",
                shadowAlertDialog.getMessage());
    }

    @Test
    public void testTodayGoalReachedPromptedYesterdayGoalSubGoal() {
        sd.setShownGoal(PAY_DAY);
        sd.setShownSubGoal(TEST_DAY);
        sd.setShownYesterdayGoal(TEST_DAY);
        activity.setGoal(GOAL_INIT);
        activity.setStepCount(GOAL_INIT+1);
        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertNotNull(alertDialog);
        ShadowAlertDialog shadowAlertDialog = Shadows.shadowOf(alertDialog);
        assertEquals("You Reached the Goal",
                shadowAlertDialog.getTitle());
        assertEquals("Congratulations! Do you want to set a new step goal?",
                shadowAlertDialog.getMessage());
    }

    @Test
    public void testTodayGoalReachedPromptedYesterdaySubGoal() {
        sd.setShownGoal(PAY_DAY);
        sd.setShownYesterdaySubGoal(TEST_DAY);
        activity.setGoal(GOAL_INIT);
        activity.setStepCount(GOAL_INIT+1);
        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertNotNull(alertDialog);
        ShadowAlertDialog shadowAlertDialog = Shadows.shadowOf(alertDialog);
        assertEquals("You Reached the Goal",
                shadowAlertDialog.getTitle());
        assertEquals("Congratulations! Do you want to set a new step goal?",
                shadowAlertDialog.getMessage());
    }

    @Test
    public void testTodayGoalReachedPromptedYesterdaySubGoalGoal() {
        sd.setShownGoal(PAY_DAY);
        sd.setShownYesterdayGoal(TEST_DAY);
        sd.setShownYesterdaySubGoal(TEST_DAY);
        activity.setGoal(GOAL_INIT);
        activity.setStepCount(GOAL_INIT+1);
        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertNotNull(alertDialog);
        ShadowAlertDialog shadowAlertDialog = Shadows.shadowOf(alertDialog);
        assertEquals("You Reached the Goal",
                shadowAlertDialog.getTitle());
        assertEquals("Congratulations! Do you want to set a new step goal?",
                shadowAlertDialog.getMessage());
    }

    @Test
    public void testTodayGoalReachedPromptedYesterdaySubGoalGoalTodaySubGoal() {
        sd.setShownGoal(PAY_DAY);
        sd.setShownYesterdayGoal(TEST_DAY);
        sd.setShownYesterdaySubGoal(TEST_DAY);
        sd.setShownSubGoal(TEST_DAY);
        activity.setGoal(GOAL_INIT);
        activity.setStepCount(GOAL_INIT+1);
        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertNotNull(alertDialog);
        ShadowAlertDialog shadowAlertDialog = Shadows.shadowOf(alertDialog);
        assertEquals("You Reached the Goal",
                shadowAlertDialog.getTitle());
        assertEquals("Congratulations! Do you want to set a new step goal?",
                shadowAlertDialog.getMessage());
    }

    @After
    public void reset() {
        editor.clear();
        editor.apply();
    }

    private class TestFitnessService extends StepCounterGoogleFit {
        private static final String TAG = "[TestFitnessService]: ";

        public TestFitnessService(MainActivity stepCountActivity) {
            super(stepCountActivity);
        }

        @Override
        public int getRequestCode() {
            return 0;
        }

        @Override
        public void setup() {
            System.out.println(TAG + "setup");
        }

        @Override
        public void updateStepCount() {
            System.out.println(TAG + "updateStepCount");
            this.activity.setStepCount(nextStepCount);
        }

    }
}
