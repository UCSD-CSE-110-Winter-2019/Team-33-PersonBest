/*
 * Created by Axel Drozdzynski
 */

package com.android.personbest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SetGoalActivity extends AppCompatActivity {

    Button cancelButton;
    Button acceptButton;
    Button setButton;
    TextView goalRecommandation;
    int stepGoal = 555;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_goal);

        initViews();
    }

    private void initViews() {
        cancelButton = findViewById(R.id.setup_goal_cancel_button);
        acceptButton = findViewById(R.id.set_goal_accept);
        setButton = findViewById(R.id.set_goal_set);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetGoalActivity.this.cancelPressed();
            }
        });
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPressed();
            }
        });
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptPressed();
            }
        });

        goalRecommandation = findViewById(R.id.recommanded_goal);
        goalRecommandation.setText(stepGoal + "");

    }

    private void acceptPressed() {

    }

    private void setPressed() {

    }

    private void cancelPressed() {

    }


}
