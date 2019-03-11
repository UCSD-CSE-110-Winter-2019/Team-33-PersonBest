package com.android.personbest.FriendshipManager;

import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FriendFireBaseAdapter implements FFireBaseAdapter, Serializable {
    String COLLECTION_KEY = "FriendList";
    String user;

    String TAG = "FriendFireBaseAdapter";

    public FriendFireBaseAdapter(String user){
        this.user = user;
    }

    public void addFriendById(String name, String id){

        Log.e(TAG,name+id);
        CollectionReference ref = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(user)
                .collection("friends");

        CollectionReference pending = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(user)
                .collection("pending");

        CollectionReference friendPending = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(id)
                .collection("pending");

        CollectionReference friendRef = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(id)
                .collection("friends");

        Map<String,String> map = new HashMap<>();
        map.put("name", name);
        Log.e(TAG, "Here Added");
        ref.add(map).addOnSuccessListener(result -> {
            Log.e(TAG, "Success added");
        }).addOnFailureListener(error -> {
            Log.e(TAG, error.getLocalizedMessage());
        });
        Log.e(TAG,"ADD FINISH");




    }
}
