package com.android.personbest.FriendshipManager;

import com.android.personbest.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class Relations implements FriendshipManager {
    public CollectionReference chat;
    StringBuilder sb;

    String COLLECTION_KEY = "PersonBestChats";
    String MESSAGE_KEY = "messages";
    String FROM_KEY;
    String TIMESTAMP_KEY = "timestamp";

    public Relations(MainActivity main) {
        if(GoogleSignIn.getLastSignedInAccount(main) == null) this.FROM_KEY = "Unknown";
        else this.FROM_KEY = GoogleSignIn.getLastSignedInAccount(main).getEmail();
    }

    public void addConvo(String email) {
        String DOCUMENT_KEY;
        if(FROM_KEY.compareTo(email) < 0) DOCUMENT_KEY = FROM_KEY + email;
        else DOCUMENT_KEY = email + FROM_KEY;

        chat = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(DOCUMENT_KEY)
                .collection(MESSAGE_KEY);
    }

    public void addFriend(String email) {

    }

    public List<String> getFriends(){
        return null;
    }

    public CollectionReference getChatHistory(String email1, String email2){
        return null;
    }
}
