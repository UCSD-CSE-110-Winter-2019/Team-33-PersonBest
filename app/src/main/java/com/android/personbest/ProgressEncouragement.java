package com.android.personbest;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

public class ProgressEncouragement {
    private Activity activity;
    public ProgressEncouragement (Activity activity) {

        this.activity = activity;
    }
    public void showEncouragementMessage(int cur, int prev){
        Context context = activity.getApplicationContext();
        if (this.progressMade(cur, prev)) {
            int progress = (cur-prev)/500;
            progress = progress*500;
            String text = "";
            if ((cur-prev)%500 == 0) {
                text = "You've increased your daily steps by " + progress + " steps. Keep up the good work!";
            }
            else {
                text = "You've increased your daily steps by over " + progress + " steps. Keep up the good work!";
            }
            Toast toast = Toast.makeText(context , text, Toast.LENGTH_LONG);
            toast.show();
        }

    }

    public boolean progressMade(int cur, int prev){
        long curLong = (long) cur, prevLong = (long) prev;
        return curLong>=(prevLong+500); // less than 500 steps is not a progress made
    }
}
