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
import com.android.personbest.SavedDataManager.SavedDataManagerFirestore;
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

        sd = new SavedDataManagerFirestore(this);
        sp = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        // we testing?
        String test_mode = sp.getString(getResources().getString(R.string.test_mode), "");
        if(test_mode.equals(getResources().getString(R.string.test_cloud))) {
            sd = new SavedDataManagerSharedPreference(this); // TODO a mock firestore adapter
        } else if (test_mode.equals(getResources().getString(R.string.test_local))) {
            sd = new SavedDataManagerSharedPreference(this);
        }

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
