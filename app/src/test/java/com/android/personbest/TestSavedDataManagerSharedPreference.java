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
    private SharedPreferences sp;
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

        Intent intent = new Intent(RuntimeEnvironment.application, MainActivity.class);
        intent.putExtra(MainActivity.FITNESS_SERVICE_KEY, TEST_SERVICE);
        activity = Robolectric.buildActivity(MainActivity.class, intent).create().get();
        //System.err.println(MainActivity.FITNESS_SERVICE_KEY);

        sd = new SavedDataManagerSharedPreference(activity);
        sp = activity.getSharedPreferences("user_data", Context.MODE_PRIVATE);
        editor = sp.edit();
        nextStepCount = 1337;
    }

    @Test
    public void testGetYesterday(){
        IDate iDate = new IDate(5);
        int day = iDate.getDay();
        SharedPreferences sp = activity.getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("4_TotalSteps",1000);
        editor.apply();
        assertEquals(sd.getYesterdaySteps(day),1000);
    }

    @Test
    public void testGetLastWeeksStep(){
        SharedPreferences sp = activity.getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        for( Integer i = 0; i < 7; i++ ){
            editor.putInt(i.toString()+"_TotalSteps",3000);
            editor.putInt(i.toString()+"_IntentionalSteps",2000);
            editor.putInt(i.toString()+"_Goal",4000);
            editor.apply();
        }

        IDate iDate =  new IDate(3);
        int day = iDate.getDay();
        List<IStatistics> history = sd.getLastWeekSteps(day);
        assertEquals(history.size(),4);
        for ( IStatistics i: history){
            assertEquals(i.getGoal(), 4000);
            assertEquals(i.getIncidentWalk(),1000);
            assertEquals(i.getIntentionalWalk(),2000);
            assertEquals(i.getStats(),"");
        }
    }

    @Test
    public void testGetStepsDaysBefore() {
        for( Integer i = 0; i < 7; i++ ){
            editor.putInt(i.toString()+"_TotalSteps",i);
            editor.apply();
        }

        IDate iDate =  new IDate(3);
        int day = iDate.getDay();
        assertEquals(3,sd.getStepsDaysBefore(3,0));
        assertEquals(2,sd.getStepsDaysBefore(day,1));
        assertEquals(1,sd.getStepsDaysBefore(day,2));
    }

    @Test
    public void testGetGoalDaysBefore() {
        for( Integer i = 0; i < 7; i++ ){
            editor.putInt(i.toString()+"_Goal",i);
            editor.apply();
        }

        IDate iDate =  new IDate(3);
        int day = iDate.getDay();
        assertEquals(3,sd.getGoalDaysBefore(3,0));
        assertEquals(2,sd.getGoalDaysBefore(day,1));
        assertEquals(1,sd.getGoalDaysBefore(day,2));
    }

    @Test
    public void testGetTodayString() {
        assertEquals(DateTimeFormatter.ofPattern("MM/dd/yyyy").format(ZonedDateTime.now()), sd.getTodayString());
    }

    @Test
    public void testGetYesterdayString() {
        assertEquals(DateTimeFormatter.ofPattern("MM/dd/yyyy").format(ZonedDateTime.now().plusDays(-1)), sd.getYesterdayString());
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
