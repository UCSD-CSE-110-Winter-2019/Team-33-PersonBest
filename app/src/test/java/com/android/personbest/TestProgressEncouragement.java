package com.android.personbest;

import android.content.Intent;
import org.apache.tools.ant.Main;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowToast;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class TestProgressEncouragement {
    private MainActivity mainActivity;

    @Before
    public void setup() {
        ExecMode.setExecMode(ExecMode.EMode.TEST_LOCAL);
        Intent intent = new Intent(RuntimeEnvironment.application, MainActivity.class);
        try {
            mainActivity = Robolectric.buildActivity(MainActivity.class, intent).create().get();
        } catch (Exception e) {
            mainActivity = Robolectric.buildActivity(MainActivity.class, intent).create().get();
        }
    }

    @Test
    public void testProgressMade() {
        ProgressEncouragement progressEncouragement = new ProgressEncouragement(mainActivity);
        assertEquals(true, progressEncouragement.progressMade(501,1));
        assertEquals(false, progressEncouragement.progressMade(500,2));
    }

    @Test
    public void testToastMessage() {
        ProgressEncouragement progressEncouragement = new ProgressEncouragement(mainActivity);
        progressEncouragement.showEncouragementMessage(503,3);
        assertEquals( "You've increased your daily steps by 500 steps. Keep up the good work!", ShadowToast.getTextOfLatestToast());
        progressEncouragement.showEncouragementMessage(505,3);
        assertEquals( "You've increased your daily steps by over 500 steps. Keep up the good work!", ShadowToast.getTextOfLatestToast());
    }
}
