package com.android.personbest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.android.personbest.SavedDataManager.SavedDataManager;
import com.android.personbest.SavedDataManager.SavedDataManagerSharedPreference;
import com.android.personbest.StepCounter.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(RobolectricTestRunner.class)
public class TestSavedDataManagerSharedPreference {
    private static final String TEST_SERVICE = "TEST_SERVICE";

    private MainActivity activity;
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
        activity = Robolectric.buildActivity(MainActivity.class, intent).create().get();
        //System.err.println(MainActivity.FITNESS_SERVICE_KEY);

        sd = new SavedDataManagerSharedPreference(activity);
        sd.clearData();
        nextStepCount = 1337;
    }

    @Test
    public void testGetYesterday(){
        IDate iDate = new DateCalendar(5);
        int day = iDate.getDay();
        SharedPreferences sp = activity.getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("4_TotalSteps",1000);
        editor.apply();
        assertEquals(sd.getYesterdaySteps(day),1000);
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
