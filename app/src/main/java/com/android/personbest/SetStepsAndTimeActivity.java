package com.android.personbest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SetStepsAndTimeActivity extends AppCompatActivity {

    private int steps;
    private String time;
    private EditText editTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_steps_and_time);

        SharedPreferences sp = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        steps = sp.getInt("StepsToday",0);

        Button addSteps = findViewById(R.id.set_steps);
        addSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                steps += 500;
            }
        });

        Button goBack = findViewById(R.id.go_back);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("user_data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("StepsToday",steps);
                editor.apply();
                finish();
            }
        });
        editTime = findViewById(R.id.set_time2);
        Button setTime = findViewById(R.id.set_time);
        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("user_data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                time = editTime.getText().toString();
                editor.putString("UnixTimeStr",time);
                editor.apply();
            }
        });
    }
}
