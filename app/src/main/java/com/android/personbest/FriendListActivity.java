package com.android.personbest;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import com.android.personbest.FriendshipManager.FFireBaseAdapter;
import com.android.personbest.FriendshipManager.FriendFireBaseAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class FriendListActivity extends ListActivity implements Observer {
    private List<String> list;
    private List<String> listName;
    private List<String> listId;
    FFireBaseAdapter fireBaseAdapter;
    String idCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        Button back = findViewById(R.id.goBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        idCurrentUser = getIntent().getStringExtra("id");
        this.fireBaseAdapter = new FriendFireBaseAdapter(idCurrentUser);
        ((FriendFireBaseAdapter)(this.fireBaseAdapter)).addObserver(this);
        this.fireBaseAdapter.getFriendlist();
        list = new ArrayList<String>();
        listId = new ArrayList<String>();
        listName = new ArrayList<String>();
        ArrayAdapter<String> myAdapter = new ArrayAdapter <String>(this,
                R.layout.row_layout, R.id.listText, list);
        setListAdapter(myAdapter);
    }


    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
        Intent intent = new Intent(this, ChatBoxActivity.class);
        String friendId = this.listId.get(position);
        String chatId = ((FriendFireBaseAdapter)(this.fireBaseAdapter)).generateIDChat(friendId);
        intent.putExtra("chatId", chatId);
        startActivity(intent);
    }


    @Override
    public void update(Observable o, Object arg) {
        String friend = (String) arg;
        String idFriend = friend.split("_")[0];
        String nameFriend = friend.split("_")[1];
        list.add(nameFriend);
        listName.add(nameFriend);
        listId.add(idFriend);
        ArrayAdapter<String> myAdapter = new ArrayAdapter <String>(this,
                R.layout.row_layout, R.id.listText, list);

        setListAdapter(myAdapter);
    }

}