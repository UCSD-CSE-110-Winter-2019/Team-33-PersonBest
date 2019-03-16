package com.android.personbest;

import android.content.Intent;
import com.android.personbest.SavedDataManager.SavedDataManager;
import com.android.personbest.SavedDataManager.SavedDataManagerSharedPreference;
import com.android.personbest.StepCounter.*;
import com.android.personbest.Timer.TimerMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowToast;

import static org.junit.Assert.assertEquals;
import static org.robolectric.RuntimeEnvironment.application;

@RunWith(RobolectricTestRunner.class)
public class TestAutomatedEncouragement {
    private static final String TAG = "[TestAutomatedEncouragement]";
    private static final String TEST_SERVICE = "TEST_SERVICE";
    private static final String TEST_DAY = "09-09-2019";
    private static final int TEST_DAY_INT = 3; // just random number
    private static final int TEST_DAY_HOUR = 19; // before 8 pm
    private static final String PAY_DAY = "01-01-2019";
    private static final int GOAL_INIT = 1024;
    private static final int HEIGHT = 40;
    private static final int NEXT_STEP_COUNT = 1337;

    private MainActivity activity;
    private SavedDataManager sd;
    private int nextStepCount;

    private IDate mockDate;
    private TimerMock mockTimer;

    @Before
    public void setUp() throws Exception {
        StepCounterFactory.put(TEST_SERVICE, new StepCounterFactory.BluePrint() {
            @Override
            public StepCounter create(MainActivity stepCountActivity) {
                return new TestAutomatedEncouragement.TestFitnessService(stepCountActivity);
            }
        });
        ExecMode.setExecMode(ExecMode.EMode.TEST_LOCAL);

        Intent intent = new Intent(application, MainActivity.class);
        try {
            activity = Robolectric.buildActivity(MainActivity.class, intent).create().get();
        } catch (Exception e) {
            activity = Robolectric.buildActivity(MainActivity.class, intent).create().get();
        }

        sd = new SavedDataManagerSharedPreference(activity);
        mockDate = new DateCalendar(TEST_DAY_INT);
        mockTimer = new TimerMock(TEST_DAY_HOUR, TEST_DAY, PAY_DAY);

        activity.setTimer(mockTimer);
        activity.setTheDate(mockDate);
        activity.setToday(TEST_DAY);

        sd.clearData();
        sd.setFirstTimeUser(false);
        sd.setUserHeight(HEIGHT, null, null);
        sd.setCurrentGoal(GOAL_INIT, null, null);
        nextStepCount = NEXT_STEP_COUNT;

    }

    /**
     * Given the user has no friends
     * When the user has reached sub goal
     * Then the legacy encouragement will show
     */
    @Test
    public void testAutomatedEnvouragement() {
        mockTimer.setTime(21);
        sd.setShownSubGoal(PAY_DAY);
        sd.setShownGoal(PAY_DAY);
        setYesterdaySteps(GOAL_INIT);
        activity.setStepCount(GOAL_INIT+2000);
        activity.setGoal(GOAL_INIT+1000+2000);
        activity.checkSubGoalReach();
        assertEquals("You've increased your daily steps by " + 2000 + " steps. Keep up the good work!",
                ShadowToast.getTextOfLatestToast());
    }

    private void setYesterdaySteps(int steps) {
        sd.setStepsByDayStr(PAY_DAY, steps, null, null);
    }

    @After
    public void reset() {
        if (mockTimer != null) {
            mockTimer.setTime(TEST_DAY_HOUR);
        }
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
