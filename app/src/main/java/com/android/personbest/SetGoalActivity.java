/*
 * Created by Axel Drozdzynski
 */

package com.android.personbest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SetGoalActivity extends AppCompatActivity {

    Button cancelButton;
    Button acceptButton;
    Button setButton;
    EditText editText;

    TextView goalRecommandation;
    int stepGoal = 555;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_goal);

        initGoal();
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
        editText = findViewById(R.id.goal_edit_text);

        goalRecommandation = findViewById(R.id.recommanded_goal);
        goalRecommandation.setText(stepGoal + "");

    }

    private void acceptPressed() {
        save(stepGoal);
        goBack();
    }

    private void setPressed() {
        final String steps = editText.getText().toString();
        try {
            save(Integer.parseInt(steps));
            goBack();
        } catch(NumberFormatException e) {
            e.printStackTrace();
            CharSequence text = "Invalid input, please try again.";
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
        }
    }

    private void cancelPressed() {
        finish();
    }

    public void initGoal() {
        int steps = 0;
        SharedPreferences sharedPreferences = this.getApplicationContext().getSharedPreferences("user_data", MODE_PRIVATE);

        steps = sharedPreferences.getInt("Current Goal", 0);
        // overflow check
        long tmpSteps = (long) steps + 500;
        if(tmpSteps == (int)tmpSteps) steps += 500;
        setGoal(steps);
    }

    public void setGoal(int num) {
        this.stepGoal = num;
    }

    public int getGoal() {
        return this.stepGoal;
    }

    public void save(int stepNumber) {
        SharedPreferences sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt("Current Goal", stepNumber);
        editor.apply();
    }

    public void goBack() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
