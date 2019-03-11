package com.android.personbest;

import android.app.ListActivity;
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
        ArrayAdapter<String> myAdapter = new ArrayAdapter <String>(this,
                R.layout.row_layout, R.id.listText, list);
        setListAdapter(myAdapter);
    }


    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
        super.onListItemClick(list, view, position, id);
    }


    @Override
    public void update(Observable o, Object arg) {
        String friend = (String) arg;
        list.add(friend);
        ArrayAdapter<String> myAdapter = new ArrayAdapter <String>(this,
                R.layout.row_layout, R.id.listText, list);

        setListAdapter(myAdapter);
    }

}