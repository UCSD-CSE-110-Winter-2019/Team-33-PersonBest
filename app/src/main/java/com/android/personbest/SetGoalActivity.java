/*
 * Created by Axel Drozdzynski
 */

package com.android.personbest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.personbest.SavedDataManager.SavedDataManager;
import com.android.personbest.SavedDataManager.SavedDataManagerFirestore;
import com.android.personbest.SavedDataManager.SavedDataManagerSharedPreference;
import com.android.personbest.Timer.ITimer;

public class SetGoalActivity extends AppCompatActivity {
    private static ExecMode.EMode test_mode;

    private Button cancelButton;
    private Button acceptButton;
    private Button setButton;
    private EditText editText;
    private SavedDataManager sd;
    private SharedPreferences sp;
    private ITimer theTimer;

    private TextView goalRecommandation;
    private int stepGoal = 555;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_goal);

        // we testing?
        test_mode = ExecMode.getExecMode();
        if(test_mode == ExecMode.EMode.TEST_CLOUD) {
            sd = new SavedDataManagerSharedPreference(this); // TODO a mock firestore adapter
        } else if (test_mode == ExecMode.EMode.TEST_LOCAL) {
            sd = new SavedDataManagerSharedPreference(this);
        }
        else {
            // set saved data manager
            sd = new SavedDataManagerFirestore(this);
        }

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
                SetGoalActivity.this.goBack();
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

    public void initGoal() {
        if(test_mode == ExecMode.EMode.DEFAULT) {
            sd.getCurrentGoal(steps -> {
                long tmpSteps = (long) steps + 500;
                if (tmpSteps == (int) tmpSteps) steps += 500;
                setGoal(steps);
            });
        }
        else {
            int steps = sd.getCurrentGoal(null);
            // overflow check, will not change if overflow
            long tmpSteps = (long) steps + 500;
            if (tmpSteps == (int) tmpSteps) steps += 500;
            setGoal(steps);
        }
    }

    public void setGoal(int num) {
        this.stepGoal = num;
        goalRecommandation = findViewById(R.id.recommanded_goal);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                goalRecommandation.setText(stepGoal + "");
            }
        });
    }

    public int getGoal() {
        return this.stepGoal;
    }

    public void save(int stepNumber) {
        sd.setCurrentGoal(stepNumber, null, null);
    }

    public void goBack() {
        Intent intent = new Intent();
        setResult(RESULT_OK,intent);
        finish();
    }

}
