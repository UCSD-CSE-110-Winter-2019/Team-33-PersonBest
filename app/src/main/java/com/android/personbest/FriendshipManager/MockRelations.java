package com.android.personbest.FriendshipManager;

public class MockRelations implements FriendshipManager {
    MockFirebaseAdapter fba;

    public MockRelations() {
        fba = new MockFirebaseAdapter("test-uid");
    }

    @Override
    public void addFriend(String name, String email) {
        fba.getDb().put(name, email);
    }

    public MockFirebaseAdapter getFba() {
        return this.fba;
    }
}
