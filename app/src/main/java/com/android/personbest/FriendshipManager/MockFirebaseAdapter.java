package com.android.personbest.FriendshipManager;

import java.util.HashMap;

public class MockFirebaseAdapter implements FFireBaseAdapter {
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
}
