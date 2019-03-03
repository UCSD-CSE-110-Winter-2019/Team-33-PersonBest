package com.android.personbest.FriendshipManager;

import com.google.firebase.firestore.CollectionReference;

import java.util.List;

public interface FriendshipManager {
    public void addConvo(String email);
    public void addFriend(String email);
    public List<String> getFriends();
    public CollectionReference getChatHistory(String email1, String email2);


}
