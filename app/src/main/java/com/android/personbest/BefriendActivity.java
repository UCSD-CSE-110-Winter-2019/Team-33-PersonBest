package com.android.personbest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.personbest.FriendshipManager.Relations;

public class BefriendActivity extends AppCompatActivity {
    private Button connect;
    private EditText nameInput;
    private EditText emailInput;
    private Relations relations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_befriend);
        connect = findViewById(R.id.connect);
        nameInput = findViewById(R.id.friendInput);
        emailInput = findViewById(R.id.emailInput);

        /*Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();*/
        String userId = getIntent().getStringExtra("id");
        relations = new Relations(userId);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String friendName = nameInput.getText().toString();
                String friendEmail = emailInput.getText().toString();
                relations.addFriend(friendName, friendEmail);
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
}
