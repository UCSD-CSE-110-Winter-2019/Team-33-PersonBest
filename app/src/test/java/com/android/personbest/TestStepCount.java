package com.android.personbest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.TextView;

import com.android.personbest.StepCounter.IDate;
import com.android.personbest.StepCounter.IStatistics;
//import com.android.personbest.StepCounter.Statistics;
import com.android.personbest.StepCounter.StepCounter;
import com.android.personbest.StepCounter.StepCounterFactory;
import com.android.personbest.StepCounter.StepCounterGoogleFit;
//import org.apache.tools.ant.Main;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

//import edu.ucsd.cse110.googlefitapp.fitness.FitnessService;
//import edu.ucsd.cse110.googlefitapp.fitness.FitnessServiceFactory;

import java.util.IdentityHashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class TestStepCount {
    private static final String TEST_SERVICE = "TEST_SERVICE";

    private MainActivity activity;
    private TextView textSteps;
    private Button btnUpdateSteps;
    private int nextStepCount;

    @Before
    public void setUp() throws Exception {
        StepCounterFactory.put(TEST_SERVICE, new StepCounterFactory.BluePrint() {
            @Override
            public StepCounter create(MainActivity stepCountActivity) {
                return new TestFitnessService(stepCountActivity);
            }
        });

        Intent intent = new Intent(RuntimeEnvironment.application, MainActivity.class);
        intent.putExtra(MainActivity.FITNESS_SERVICE_KEY, TEST_SERVICE);
        //System.err.println(MainActivity.FITNESS_SERVICE_KEY);

        activity = Robolectric.buildActivity(MainActivity.class, intent).create().get();
        textSteps = activity.findViewById(R.id.stepsTodayVal);
        btnUpdateSteps = activity.findViewById(R.id.btnUpdateSteps);
        nextStepCount = 1337;
    }

    @Test
    public void testUpdateStepsButton() {
        //System.out.println("Test is running here");
        btnUpdateSteps.performClick();
        assertEquals("1337", textSteps.getText().toString());
    }

    @Test
    public void testGetYesterday(){
        IDate iDate = new IDate(5);
        int day = iDate.getDay();
        SharedPreferences sp = activity.getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("4_TotalSteps",1000);
        editor.apply();
        StepCounterGoogleFit stepCounter = (StepCounterGoogleFit) activity.getStepCounter();
        assertEquals(stepCounter.getYesterdaySteps(day),1000);
    }

    @Test
    public void testGetLastWeeksStep(){
        SharedPreferences sp = activity.getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        for( Integer i = 0; i < 7; i++ ){
            editor.putInt(i.toString()+"_TotalSteps",3000);
            editor.putInt(i.toString()+"_IntentionalSteps",2000);
            editor.putInt(i.toString()+"_Goal",4000);
            editor.putFloat(i.toString()+"_AverageMPH", (float) 2.1);
            editor.putLong(i.toString()+"_ExerciseTime", 8908L);
            editor.apply();
        }

        IDate iDate =  new IDate(3);
        int day = iDate.getDay();
        StepCounterGoogleFit stepCounter = (StepCounterGoogleFit) activity.getStepCounter();
        List<IStatistics> history = stepCounter.getLastWeekSteps(day);
        assertEquals(history.size(),4);
        for ( IStatistics i: history){
            assertEquals(i.getGoal(), 4000);
            assertEquals(i.getIncidentWalk(),1000);
            assertEquals(i.getIntentionalWalk(),2000);
            assertEquals(i.getStats(),"MPH: 2.1");
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
