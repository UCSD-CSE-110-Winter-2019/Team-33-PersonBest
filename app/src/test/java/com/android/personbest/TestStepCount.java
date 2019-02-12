package com.android.personbest;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import com.android.personbest.StepCounter.IStatistics;
//import com.android.personbest.StepCounter.Statistics;
import com.android.personbest.StepCounter.StepCounter;
import com.android.personbest.StepCounter.StepCounterFactory;
//import org.apache.tools.ant.Main;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

//import edu.ucsd.cse110.googlefitapp.fitness.FitnessService;
//import edu.ucsd.cse110.googlefitapp.fitness.FitnessServiceFactory;

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
        //assertEquals("steps will be shown here", textSteps.getText().toString());
        //System.out.println("Test is running here");
        btnUpdateSteps.performClick();
        assertEquals("1337", textSteps.getText().toString());
    }

    private class TestFitnessService implements StepCounter {
        private static final String TAG = "[TestFitnessService]: ";
        private MainActivity stepCountActivity;

        public TestFitnessService(MainActivity stepCountActivity) {
            this.stepCountActivity = stepCountActivity;
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
            stepCountActivity.setStepCount(nextStepCount);
            System.out.println(nextStepCount);
        }

        public int getYesterdaySteps(){
            return 0;
        }

        @Override
        public List<IStatistics> getLastWeekSteps() {
            return null;
        }
    }
}
