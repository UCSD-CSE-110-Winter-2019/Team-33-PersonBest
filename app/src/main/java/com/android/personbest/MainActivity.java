package com.android.personbest;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public double calculateStrideLength(){
        editText = (EditText)findViewById(R.id.height);
        double height = Double.parseDouble(editText.getText().toString());
        return 0.414*height;
    }
}
