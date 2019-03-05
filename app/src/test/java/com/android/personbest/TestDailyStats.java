package com.android.personbest;

import android.content.Intent;

import com.android.personbest.SavedDataManager.SavedDataManager;
import com.android.personbest.SavedDataManager.SavedDataManagerSharedPreference;
import com.android.personbest.StepCounter.*;

import com.android.personbest.Timer.ITimer;
import com.android.personbest.Timer.TimerMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class TestDailyStats {
    private static final String TEST_SERVICE = "TEST_SERVICE";
    private static final int TEST_DAT_INT = 3;
    private MainActivity mainActivity;
    private static final int TEN_K = 10000;
    private static final int FIVE_K = 5000;
    private SavedDataManager sd;
    private ITimer timer;

    @Before
    public void setup() {

        StepCounterFactory.put(TEST_SERVICE, new StepCounterFactory.BluePrint() {
            @Override
            public StepCounter create(MainActivity stepCountActivity) {
                return new StepCounterGoogleFit(stepCountActivity);
            }
        });

        ExecMode.setExecMode(ExecMode.EMode.TEST_LOCAL);

        timer = new TimerMock(0, "03/03/2019", "02/03/2019");

        Intent intent = new Intent(RuntimeEnvironment.application, MainActivity.class);
        intent.putExtra(MainActivity.FITNESS_SERVICE_KEY, TEST_SERVICE);
        //System.err.println(MainActivity.FITNESS_SERVICE_KEY);
        mainActivity = Robolectric.buildActivity(MainActivity.class, intent).create().get();
        sd = new SavedDataManagerSharedPreference(mainActivity);
        mainActivity.setTimer(timer);
    }

    @Test
    public void testSetGoal() {
        mainActivity.setGoal(10000);
        assertEquals(TEN_K, sd.getGoalByDayStr(timer.getTodayString(), null));
    }
}
