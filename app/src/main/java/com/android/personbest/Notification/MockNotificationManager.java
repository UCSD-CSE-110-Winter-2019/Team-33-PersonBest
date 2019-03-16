package com.android.personbest.Notification;

public class MockNotificationManager implements INotification {
    public boolean pushed = false;
    @Override
    public void sendNotification(String msg) {
        pushed = true;
    }
}
