package com.android.personbest;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class ProgressEncouragementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_encouragement);
    }
    public void showEncouragementMessage(){
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context , "Congratulation! You have made significant progress!", Toast.LENGTH_LONG);
        toast.show();
    }

    public boolean progressMade(){
        int yesterday = 0;
        int today = 0;
        return yesterday<today;
    }
}
