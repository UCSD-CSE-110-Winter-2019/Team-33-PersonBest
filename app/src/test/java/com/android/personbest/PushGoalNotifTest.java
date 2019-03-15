package com.android.personbest;

import android.content.Intent;
import com.android.personbest.SavedDataManager.SavedDataManager;
import com.android.personbest.SavedDataManager.SavedDataManagerSharedPreference;
import com.android.personbest.StepCounter.StepCounter;
import com.android.personbest.StepCounter.StepCounterFactory;
import com.android.personbest.StepCounter.StepCounterGoogleFit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static junit.framework.TestCase.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class PushGoalNotifTest {
    private MainActivity mainActivity;

    private static final String TEST_SERVICE = "TEST_SERVICE";
    private SavedDataManager sd;

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
        try {
            mainActivity = Robolectric.buildActivity(MainActivity.class, intent).create().get();
        } catch (Exception e) {
            mainActivity = Robolectric.buildActivity(MainActivity.class, intent).create().get();
        }
        sd = new SavedDataManagerSharedPreference(mainActivity);
        sd.setUserHeight(72, null, null);
        mainActivity.goalCheckService = new MockService();
        mainActivity.setStepCount(0);
        mainActivity.setGoal(500);
    }

    @Test
    public void testGoalNotify() {
        this.mainActivity.setStepCount(600);
        assertTrue(this.mainActivity.goalCheckService.pushed);
    }

    public class MockService extends GoalCheckService {
        @Override
        public void update(int steps, int goal) {
            if(steps >= goal) this.pushed = true;
        }
    }
}
