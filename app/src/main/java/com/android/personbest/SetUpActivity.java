package com.android.personbest;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.personbest.SavedDataManager.SavedDataManager;
import com.android.personbest.SavedDataManager.SavedDataManagerSharedPreference;

public class SetUpActivity extends AppCompatActivity {
    private EditText editText;
    private Button saveButton;
    private SavedDataManager sd;
    private SharedPreferences sp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

        saveButton = findViewById(R.id.save_button);
        editText = findViewById(R.id.height);

        // we testing?
        ExecMode.EMode test_mode = ExecMode.getExecMode();
        if(test_mode == ExecMode.EMode.TEST_CLOUD) {
            sd = new SavedDataManagerSharedPreference(this); // TODO a mock firestore adapter
        } else if (test_mode == ExecMode.EMode.TEST_LOCAL) {
            sd = new SavedDataManagerSharedPreference(this);
        }

        sd = new SavedDataManagerSharedPreference(this);
        sd.setCurrentGoal(5000);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(save()) finish();
            }
        });
    }

    public boolean save() {
        if(editText.getText().toString().length() == 0) return false;
        int height = Integer.parseInt(editText.getText().toString());
        sd.setUserHeight(height);
        Toast.makeText(SetUpActivity.this, "Height Saved!", Toast.LENGTH_SHORT).show();
        return true;
    }
}
