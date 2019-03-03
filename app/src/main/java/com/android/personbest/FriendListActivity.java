package com.android.personbest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.firebase.FirebaseApp;

public class FriendListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_friend_list);
    }
}
