/*
 * Created by Axel Drozdzynski
 */

package com.android.personbest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class SetGoalActivity extends AppCompatActivity {

    Button cancelButton;
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
        goalRecommandation = findViewById(R.id.recommanded_goal);
        goalRecommandation.setText(stepGoal + "");
    }


}
