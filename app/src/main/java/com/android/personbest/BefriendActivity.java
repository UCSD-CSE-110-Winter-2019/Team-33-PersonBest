package com.android.personbest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class BefriendActivity extends AppCompatActivity {
    private Button connect;
    private EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_befriend);
        connect = findViewById(R.id.connect);
        input = findViewById(R.id.friendInput);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toxicFriend = input.getText().toString();
            }
        });
    }
}
