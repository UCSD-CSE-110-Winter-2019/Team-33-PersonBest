package com.android.personbest.FriendshipManager;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;


public class FriendFireBaseAdapter implements FFireBaseAdapter {
    CollectionReference friendList;
    String COLLECTION_KEY = "FriendList";
    String user;

    public FriendFireBaseAdapter(CollectionReference friendList, String user){
        this.friendList = friendList;
        this.user = user;
    }

    public void addFriend(String id){
        CollectionReference ref = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(user)
                .collection("friends");

        Map<String,String> map = new HashMap<>();
        map.put(user,id);

        ref.add(map).addOnSuccessListener(result -> {}).addOnFailureListener(error -> {
            Log.e(TAG, error.getLocalizedMessage());
        });
    }


}
