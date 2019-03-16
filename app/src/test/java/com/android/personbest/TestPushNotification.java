package com.android.personbest;

import com.android.personbest.Notification.INotification;
import com.android.personbest.Notification.MockNotificationManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class TestPushNotification {
    @Test
    public void testNotification(){
        INotification mockNotificationManager = new MockNotificationManager();
        assertTrue(!((MockNotificationManager) mockNotificationManager).pushed);
        mockNotificationManager.sendNotification("haha");
        assertTrue(((MockNotificationManager) mockNotificationManager).pushed);
    }
}
