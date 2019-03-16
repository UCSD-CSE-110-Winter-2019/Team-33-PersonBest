package com.android.personbest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

@RunWith(JUnit4.class)
public class PushGoalNotifUnitTest {
    @Test
    public void testLogic() {
        assertFalse(copiedMethod(0, 500));
        assertTrue(copiedMethod(600, 500));
    }

    public boolean copiedMethod(int steps, int goal) {
        return steps >= goal;
    }
}
