package com.android.personbest.FriendshipManager;

import android.util.Log;

import com.android.personbest.ChatBoxActivity;
import com.android.personbest.MainActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;
import java.util.Map;
import java.util.Observable;

import static android.content.ContentValues.TAG;

public class FirebaseMessagingAdapter extends Observable implements IChat {

    public CollectionReference chat;
    boolean res = false;
    StringBuilder stringBuilder;

    String FROM_KEY = "from";
    String TEXT_KEY = "text";
    String COLLECTION_KEY = "chats";
    String MESSAGES_KEY = "messages";
    String TIMESTAMP_KEY = "timestamp";

    public FirebaseMessagingAdapter(String chatId){
        chat = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(chatId)
                .collection(MESSAGES_KEY);
    }


    @Override
    public boolean add(Map<String, String> message) {
        this.chat.add(message).addOnSuccessListener(result -> {
            setChanged();
            notifyObservers("");
        }).addOnFailureListener(error -> {
            Log.e(ChatBoxActivity.class.getSimpleName(),error.getLocalizedMessage());
        });
        return res;
    }

    @Override
    public StringBuilder orderBy() {
        stringBuilder = null;
        chat.orderBy(TIMESTAMP_KEY, Query.Direction.ASCENDING)
                .addSnapshotListener((newChatSnapShot, error) -> {
                    if (error != null) {
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

                        setChanged();
                        notifyObservers(sb.toString());
                    }
                });
        return stringBuilder;
    }


    public void subscribeToTopic(String topic){

        FirebaseMessaging.getInstance ().subscribeToTopic( topic ) .addOnCompleteListener(task -> {
            String msg =  "Subscribed to notifications";
            if  (!task.isSuccessful()) {
                msg =  "Subscribe to notifications failed" ; }
            Log. d ( TAG , msg); }
        );
    }
}
