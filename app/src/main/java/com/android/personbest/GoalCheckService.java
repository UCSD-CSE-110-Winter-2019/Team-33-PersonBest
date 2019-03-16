package com.android.personbest;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.LocalServerSocket;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.support.v4.app.NotificationCompat;

public class GoalCheckService extends Service {

    private final String TAG = "GoalCheckService";
    String ACHIEVE_MSG = "Achieve the Goal! Congrats! Click Here to set a new Goal!";
    private final IBinder iBinder = new LocalService();
    private int steps;
    private int goal;
    public boolean pushed = false;

    public GoalCheckService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return iBinder;
    }

    public void update(int steps, int goal) {
        this.steps = steps;
        this.goal = goal;

        if (steps >= goal) {
            // Call Notification
            Log.d(TAG, "Achieved Goal");
            if(!pushed) {
                pushed = true;
                sendNotification();
            }
        }
        else {
            pushed = false;
            Log.d(TAG, "Not Achieve the goal");
        }

    }

    class LocalService extends Binder {
        public GoalCheckService getService() {
            return GoalCheckService.this;
        }
    }

    private void sendNotification() {
        Log.e(TAG,"SEND NOTIFICATION");
        Intent intent = new Intent(this, SetGoalActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        Intent snoozeIntent = new Intent(this, MainActivity.class);
        PendingIntent snoozePendingIntent =
                PendingIntent.getBroadcast(this, 0, snoozeIntent, 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,"0")
                .setSmallIcon(R.drawable.ham_2x)
                .setContentTitle("Person Best Goal")
                .setContentText(this.ACHIEVE_MSG)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ham_2x, "Snooze",
                        snoozePendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}
