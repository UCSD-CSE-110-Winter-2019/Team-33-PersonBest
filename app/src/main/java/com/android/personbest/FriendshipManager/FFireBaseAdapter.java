package com.android.personbest.FriendshipManager;

public interface FFireBaseAdapter {
    void addFriendById(String name, String id);
    void getFriendlist();
    void hasFriend(OperatorBoolean b);
    String generateIDChat(String idFriend);
}
