package com.android.personbest;

import org.junit.Test;
import org.robolectric.shadows.ShadowToast;

import static org.junit.Assert.assertEquals;

public class TestProgressEncouragement {

    @Test
    public void testProgressMade() {
        ProgressEncouragement progressEncouragement = new ProgressEncouragement();
        assertEquals(true, progressEncouragement.progressMade(2,1));
        assertEquals(false, progressEncouragement.progressMade(1,2));
    }
}
