package com.android.personbest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.android.personbest.FriendshipManager.Relations;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.FirebaseApp;

import java.util.ArrayList;
import java.util.List;

public class FriendListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_friend_list);
    }

    public List<String> getFriendList(){
        Relations relations = new Relations(new MainActivity());
        List<String> listEmail = relations.getFriends();
        List<String> listFriendsName = new ArrayList<String>();
        for (String email: listEmail) {
            GoogleSignInAccount account = GoogleSignInAccount.
            listFriendsName.add(account.);
        }
        return listFriendsName;
    }
}
