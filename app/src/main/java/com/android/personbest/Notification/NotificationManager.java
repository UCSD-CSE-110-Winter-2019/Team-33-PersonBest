package com.android.personbest.Notification;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.personbest.MainActivity;
import com.android.personbest.R;
import com.android.personbest.SetGoalActivity;

public class NotificationManager implements INotification {
    private final String TAG = "NotificationManager";
    private MainActivity activity;

    public NotificationManager(MainActivity activity){
        this.activity = activity;
    }
    @Override
    public void sendNotification(String msg) {
        Log.e(TAG,"SEND NOTIFICATION");
        Intent intent = new Intent(this.activity, SetGoalActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this.activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        Intent snoozeIntent = new Intent(this.activity, MainActivity.class);
        PendingIntent snoozePendingIntent =
                PendingIntent.getBroadcast(this.activity, 0, snoozeIntent, 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this.activity,"0")
                .setSmallIcon(R.drawable.ham_2x)
                .setContentTitle("Person Best Goal")
                .setContentText(msg)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ham_2x, "Snooze",
                        snoozePendingIntent);
        android.app.NotificationManager notificationManager = (android.app.NotificationManager) this.activity.getSystemService(this.activity.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}
