package com.android.personbest;

import android.content.Intent;

import com.android.personbest.FriendshipManager.MockFirebaseAdapter;
import com.android.personbest.FriendshipManager.MockRelations;
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

@RunWith(RobolectricTestRunner.class)
public class TestEncourageFriends {
    MainActivity mainActivity;
    FriendListActivity activity;

    private static final String TEST_SERVICE = "TEST_SERVICE";
    private SavedDataManager sd;
    @Before
    public void setUp() throws Exception {
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
    }
    @Test
    public void testEncouragementPossible(){
        MockRelations relations = new MockRelations();
        relations.addFriend("Holmes","holmes@gmail.com");
        MockFirebaseAdapter fba = relations.getFba();

    }
}
