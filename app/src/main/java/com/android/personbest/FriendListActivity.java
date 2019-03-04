package com.android.personbest;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.personbest.Adapters.FriendListAdapter;
import com.android.personbest.Models.FriendModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FriendListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_friend_list);
        initLayouts();
    }

    protected void initLayouts() {
        initRefresh();
        initRecycler();
    }

    protected void initRecycler() {
        Query query = db.collection("friendsListTest");
        recyclerView = findViewById(R.id.friends_recycler);
        LinearLayoutManager layoutManager = new GridLayoutManager(this.getApplicationContext(), 1);
        layoutManager.setReverseLayout(true);
        //layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        FirestoreRecyclerOptions<FriendModel> options = new FirestoreRecyclerOptions.Builder<FriendModel>()
                .setQuery(query, FriendModel.class)
                .build();
        recyclerView.setAdapter(new FriendListAdapter(options));
    }

    public void initRefresh() {
        final SwipeRefreshLayout mySwipeToRefresh = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        mySwipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mySwipeToRefresh.setRefreshing(false);
            }
        });
    }

}
