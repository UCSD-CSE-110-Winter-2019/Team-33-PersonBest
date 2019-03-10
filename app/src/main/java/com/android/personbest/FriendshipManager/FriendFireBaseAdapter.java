package com.android.personbest.FriendshipManager;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;


public class FriendFireBaseAdapter implements FFireBaseAdapter, Serializable {
    String COLLECTION_KEY = "FriendList";
    String user;

    public FriendFireBaseAdapter(String user){
        this.user = user;
    }

    public void addFriendById(String name, String id){
        CollectionReference ref = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(user)
                .collection("friends");

        CollectionReference pending = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(user)
                .collection("pending");

        // Check if the user is already in the friend's request.
        boolean bePendingFriend = false;


        Map<String,String> map = new HashMap<>();
        map.put(id,name);

        ref.add(map).addOnSuccessListener(result -> {}).addOnFailureListener(error -> {
            Log.e(TAG, error.getLocalizedMessage());
        });
    }

    private boolean isPendingFriend(String user, String friend){

        CollectionReference friendPending = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(friend)
                .collection("pending");



        return false;
    }
}
