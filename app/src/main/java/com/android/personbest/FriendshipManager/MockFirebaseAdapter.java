package com.android.personbest.FriendshipManager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Observable;

public class MockFirebaseAdapter extends Observable implements FFireBaseAdapter, Serializable {
    HashMap<String, String> db;

    public MockFirebaseAdapter() {
        this.db = new HashMap<>();
    }

    @Override
    public void addFriendById(String name, String email) {
        this.db.put(name, email);
    }

    @Override
    public void getFriendlist() {}

    public HashMap<String, String> getDb() {
        return this.db;
    }

    public void hasFriend(OperatorBoolean b) {
        b.op(db.size() > 0);
    }

}
