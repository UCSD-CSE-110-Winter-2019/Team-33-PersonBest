package com.android.personbest;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;

import com.android.personbest.FriendshipManager.FFireBaseAdapter;
import com.android.personbest.FriendshipManager.FriendshipManager;
import com.android.personbest.FriendshipManager.MockFirebaseAdapter;
import com.android.personbest.FriendshipManager.MockRelations;
import com.android.personbest.FriendshipManager.Relations;
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

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class TestSocialAspect {
    Activity mainActivity;
    BefriendActivity activity;

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
    }

    @Test
    public void testAddFriend() {
        Intent secondaryIntent = new Intent(this.mainActivity, BefriendActivity.class);
        this.activity = Robolectric.buildActivity(BefriendActivity.class, secondaryIntent).create().get();

        EditText et = activity.findViewById(R.id.friendInput);
        et.setText("Holmes");
        et = activity.findViewById(R.id.emailInput);
        et.setText("holmes@gmail.com");
        Button button = activity.findViewById(R.id.connect);

        activity.setRelations(new MockRelations());
        MockRelations relations = (MockRelations)activity.getRelations();
        MockFirebaseAdapter fba = relations.getFba();

        button.performClick();
        assertEquals("holmes@gmail.com", fba.getDb().get("Holmes"));
    }
}
