package com.android.personbest;

import android.content.ContentValues;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

public class SetUpActivity extends AppCompatActivity {
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);
    }

    public double calculateStrideLength(){
        editText = (EditText)findViewById(R.id.height);
        double height = Double.parseDouble(editText.getText().toString());
        return 0.414*height;
    }
}

