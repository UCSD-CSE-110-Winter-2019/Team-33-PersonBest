package com.android.personbest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SetStepsAndTimeActivity extends AppCompatActivity {

    private int steps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_steps_and_time);

        Intent intent = getIntent();
        this.steps =  intent.getIntExtra("stepsToday",0);

        Button addSteps = findViewById(R.id.set_steps);
        addSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                steps += 500;
            }
        });

        //Button goBack = ;
    }
}
