package com.android.personbest;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetUpActivity extends AppCompatActivity {
    private EditText editText;
    private Button saveButton;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

        saveButton = findViewById(R.id.save_button);
        editText = findViewById(R.id.height);

        sp = getPreferences(Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putInt("Current Goal", 5000);

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
        editor.putInt("Height", height);
        editor.apply();
        Toast.makeText(SetUpActivity.this, "Height Saved!", Toast.LENGTH_SHORT).show();
        return true;
    }
}
