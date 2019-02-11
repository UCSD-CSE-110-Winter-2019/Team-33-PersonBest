package com.android.personbest;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetUpActivity extends AppCompatActivity {
    private EditText editText;
    private Button saveButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_activity);

        initViews();
        initPreferences();
        if (saveButton != null)
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    save();
                }
            });

    }

    private void initViews() {
        saveButton = findViewById(R.id.save_button);
        editText = (EditText)findViewById(R.id.height);
    }

    public float calculateStrideLength(float height) {
            return (float)(0.414 * height);
    }

    public void initPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);

        Float a = sharedPreferences.getFloat("height", 0);
        if (editText != null) {
            editText.setText(a.toString());
        }
    }

    public void save() {

        SharedPreferences sharedPreferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        float height = Float.parseFloat(editText.getText().toString());
        float stride = calculateStrideLength(height);

        editor.putFloat("stride", stride);
        editor.putFloat("height", height);


        editor.apply();
        Toast.makeText(SetUpActivity.this, "Saved", Toast.LENGTH_SHORT).show();
    }
}
