package com.android.personbest;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CongratsActivity extends AppCompatActivity {
    Button notYetButton;
    Button yesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congrats);
        initViews();
    }

    protected void initViews() {
        notYetButton = findViewById(R.id.congrats_notyet);
        yesButton = findViewById(R.id.congrats_yes);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yesButtonPressed();
            }
        });
        notYetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notYetButtonPressed();
            }
        });
    }

    void yesButtonPressed() {
        final Activity activity = this;
        Intent intent = new Intent(activity, SetGoalActivity.class);
        activity.startActivity(intent);
    }

    void notYetButtonPressed () {
        //un comment this line when you merge
        // finish();
    }

}
