package com.android.personbest.FriendshipManager;

import android.util.Log;
import android.util.Pair;

import com.android.personbest.MainActivity;
import com.android.personbest.SavedDataManager.SavedDataManager;
import com.android.personbest.SavedDataManager.SavedDataManagerSharedPreference;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

public class Relations implements FriendshipManager {
    public CollectionReference chat;

    String COLLECTION_KEY = "FriendList";
    String MESSAGE_KEY = "messages";
    String FROM_KEY = "from";
    String TEXT_KEY = "text";
    String TIMESTAMP_KEY = "timestamp";

    SavedDataManager sdm;

    String from;

    public Relations(MainActivity main, SavedDataManager sdm) {
        if(GoogleSignIn.getLastSignedInAccount(main) == null) this.from = "Unknown";
        else this.from = GoogleSignIn.getLastSignedInAccount(main).getId();

        this.sdm = sdm;
    }

    /*public void addConvo(String email) {
        String DOCUMENT_KEY;
        if(from.compareTo(email) < 0) DOCUMENT_KEY = from + " " + email;
        else DOCUMENT_KEY = email + " " + from;

        FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(DOCUMENT_KEY)
                .collection(MESSAGE_KEY);

        initMessageUpdateListener();
        subscribeToNotificationsTopic(DOCUMENT_KEY);
    }*/

    public void addFriend(String email) {
        String friendId = sdm.getIdByEmail(email);
        if(friendId == null) return;

        CollectionReference ref = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(from)
                .collection("friends");

        Map<String,String> map = new HashMap<String, String>();
        map.put(from,friendId);

        ref.add(map).addOnSuccessListener(result -> {}).addOnFailureListener(error -> {
            Log.e(TAG, error.getLocalizedMessage());
        });
    }

    /*public List<String> getFriends() {
        CollectionReference ref = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(from)
                .collection("friends");

        ArrayList<String> list = new ArrayList<String>();

        ref.addSnapshotListener((newChatSnapShot, error) -> {
                    if (error != null) {
                        Log.e(TAG, error.getLocalizedMessage());
                        return;
                    }

                    if (newChatSnapShot != null && !newChatSnapShot.isEmpty()) {
                        List<DocumentChange> documentChanges = newChatSnapShot.getDocumentChanges();
                        documentChanges.forEach(change -> {
                            QueryDocumentSnapshot document = change.getDocument();
                            list.add((String)document.get(from));
                        });
                    }
                });

        return list;
    }

    public CollectionReference getChatHistory(String email1, String email2) {
        String DOCUMENT_KEY;
        if(email1.compareTo(email2) < 0) DOCUMENT_KEY = email1 + " " + email2;
        else DOCUMENT_KEY = email2 + " " + email1;

        return FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(DOCUMENT_KEY)
                .collection(MESSAGE_KEY);
    }

    private void initMessageUpdateListener() {
        chat.orderBy(TIMESTAMP_KEY, Query.Direction.ASCENDING)
                .addSnapshotListener((newChatSnapShot, error) -> {
                    if (error != null) {
                        Log.e(TAG, error.getLocalizedMessage());
                        return;
                    }

                    if (newChatSnapShot != null && !newChatSnapShot.isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        List<DocumentChange> documentChanges = newChatSnapShot.getDocumentChanges();
                        documentChanges.forEach(change -> {
                            QueryDocumentSnapshot document = change.getDocument();
                            sb.append(document.get(FROM_KEY));
                            sb.append(":\n");
                            sb.append(document.get(TEXT_KEY));
                            sb.append("\n");
                            sb.append("---\n");
                        });
                    }
                });
    }

    private void subscribeToNotificationsTopic(String DOCUMENT_KEY) {
        FirebaseMessaging.getInstance().subscribeToTopic(DOCUMENT_KEY)
                .addOnCompleteListener(task -> {
                            String msg = "Subscribed to notifications";
                            if (!task.isSuccessful()) {
                                msg = "Subscribe to notifications failed";
                            }
                            Log.d(TAG, msg);
                        }
                );
    }*/
}
