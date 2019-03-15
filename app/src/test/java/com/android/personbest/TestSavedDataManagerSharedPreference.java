package com.android.personbest;

import android.content.Intent;
import android.content.SharedPreferences;
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

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(RobolectricTestRunner.class)
public class TestSavedDataManagerSharedPreference {
    private static final String TEST_SERVICE = "TEST_SERVICE";

    private MainActivity mainActivity;
    private SavedDataManager sd;
    private SharedPreferences.Editor editor;
    private int nextStepCount;
    private String theTestDay = "02/19/2019";
    private String thePayDay = "02/01/2019";

    @Before
    public void setUp() throws Exception {
        StepCounterFactory.put(TEST_SERVICE, new StepCounterFactory.BluePrint() {
            @Override
            public StepCounter create(MainActivity stepCountActivity) {
                return new TestSavedDataManagerSharedPreference.TestFitnessService(stepCountActivity);
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
        //System.err.println(MainActivity.FITNESS_SERVICE_KEY);

        sd = new SavedDataManagerSharedPreference(mainActivity);
        sd.clearData();
        nextStepCount = 1337;
    }

    @Test
    public void testYesterdaySteps(){
        ITimer timer = new TimerMock(0, "03/03/2019", "03/02/2019");
        sd.setStepsByDayStr(timer.getYesterdayString(), 1000, null, null);
        assertEquals(sd.getStepsByDayStr(timer.getYesterdayString(), null),1000);
    }

    @Test
    public void testIsShownGoal() {
        assertFalse(sd.isShownGoal(theTestDay));
        sd.setShownGoal(thePayDay);
        assertFalse(sd.isShownGoal(theTestDay));
        sd.setShownGoal(theTestDay);
        assertTrue(sd.isShownGoal(theTestDay));
    }

    @Test
    public void testIsShownSubGoal() {
        assertFalse(sd.isShownSubGoal(theTestDay));
        sd.setShownSubGoal(thePayDay);
        assertFalse(sd.isShownSubGoal(theTestDay));
        sd.setShownSubGoal(theTestDay);
        assertTrue(sd.isShownSubGoal(theTestDay));
    }

    @Test
    public void testIsCheckedYesterdayGoal() {
        assertFalse(sd.isCheckedYesterdayGoal(theTestDay));
        sd.setCheckedYesterdayGoal(thePayDay);
        assertFalse(sd.isCheckedYesterdayGoal(theTestDay));
        sd.setCheckedYesterdayGoal(theTestDay);
        assertTrue(sd.isCheckedYesterdayGoal(theTestDay));
    }

    @Test
    public void testIsShownYesterdayGoal() {
        assertFalse(sd.isShownYesterdayGoal(theTestDay));
        sd.setShownYesterdayGoal(thePayDay);
        assertFalse(sd.isShownYesterdayGoal(theTestDay));
        sd.setShownYesterdayGoal(theTestDay);
        assertTrue(sd.isShownYesterdayGoal(theTestDay));
    }

    @Test
    public void testIsCheckedYesterdaySubGoal() {
        assertFalse(sd.isCheckedYesterdaySubGoal(theTestDay));
        sd.setCheckedYesterdaySubGoal(thePayDay);
        assertFalse(sd.isCheckedYesterdaySubGoal(theTestDay));
        sd.setCheckedYesterdaySubGoal(theTestDay);
        assertTrue(sd.isCheckedYesterdaySubGoal(theTestDay));
    }

    @Test
    public void testIsShownYesterdaySubGoal() {
        assertFalse(sd.isShownYesterdaySubGoal(theTestDay));
        sd.setShownYesterdaySubGoal(thePayDay);
        assertFalse(sd.isShownYesterdaySubGoal(theTestDay));
        sd.setShownYesterdaySubGoal(theTestDay);
        assertTrue(sd.isShownYesterdaySubGoal(theTestDay));
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
