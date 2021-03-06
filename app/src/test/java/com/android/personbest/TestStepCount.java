package com.android.personbest;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

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

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class TestStepCount {
    private static final String TEST_SERVICE = "TEST_SERVICE";

    private MainActivity mainActivity;
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

        ExecMode.setExecMode(ExecMode.EMode.TEST_LOCAL);

        Intent intent = new Intent(RuntimeEnvironment.application, MainActivity.class);
        intent.putExtra(MainActivity.FITNESS_SERVICE_KEY, TEST_SERVICE);

        try {
            mainActivity = Robolectric.buildActivity(MainActivity.class, intent).create().get();
        } catch (Exception e) {
            mainActivity = Robolectric.buildActivity(MainActivity.class, intent).create().get();
        }
        textSteps = mainActivity.findViewById(R.id.stepsTodayVal);
        btnUpdateSteps = mainActivity.findViewById(R.id.btnUpdateSteps);
        nextStepCount = 1337;
    }

    @Test
    public void testUpdateStepsButton() {
        //System.out.println("Test is running here");
        btnUpdateSteps.performClick();
        assertEquals("1337", textSteps.getText().toString());
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
