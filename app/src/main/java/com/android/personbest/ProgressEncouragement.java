package com.android.personbest;

import android.content.Context;
import android.widget.Toast;

public class ProgressEncouragement {
    private MainActivity mainActivity;
    public void showEncouragementMessage(){
        Context context = mainActivity.getApplicationContext();
        Toast toast = Toast.makeText(context , "Congratulation! You have made significant progress!", Toast.LENGTH_LONG);
        toast.show();
    }

    public boolean progressMade(int today, int yesterday){
        return today>yesterday;
    }
}
