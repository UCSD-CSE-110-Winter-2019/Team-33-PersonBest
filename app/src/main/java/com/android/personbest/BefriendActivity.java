package com.android.personbest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.personbest.FriendshipManager.Relations;

public class BefriendActivity extends AppCompatActivity {
    private Button connect;
    private EditText input;
    private Relations relations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_befriend);
        connect = findViewById(R.id.connect);
        input = findViewById(R.id.friendInput);

        relations = ((Relations)(getIntent().getSerializableExtra("FriendListManager")));

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toxicFriend = input.getText().toString();
                relations.addFriend(toxicFriend);
            }
        });
    }
}
