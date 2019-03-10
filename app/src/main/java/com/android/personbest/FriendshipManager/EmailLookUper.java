package com.android.personbest.FriendshipManager;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.personbest.SavedDataManager.SavedDataOperatorString;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.io.Serializable;
import java.util.HashMap;

public class EmailLookUper implements Email2Id, Serializable {

    private FirebaseFirestore ff;
    private static final String DOCKEY_EMAILS = "emails";
    private static final String FIEKEY_EMAILMAP = "email_map";
    private static final String TAG = "EmailLookUper";
    private CollectionReference ffUserData;


    public EmailLookUper(){
        this.ff = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        ff.setFirestoreSettings(settings);
        ffUserData = ff.collection("user_data");
    }

    public void getIdByEmail(String email, SavedDataOperatorString callback) {
        final String query = cleanEmailStr(email);
        final SavedDataOperatorString cb = callback;
        ffUserData.document(DOCKEY_EMAILS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                HashMap<String, String> ma = (HashMap<String, String>) document.getData().get(FIEKEY_EMAILMAP);
                                Log.d(TAG, "DocumentSnapshot data: " + ma);
                                if (ma.containsKey(query)) {
                                    cb.op(ma.get(query));
                                } else {
                                    cb.op(null);
                                }
                            } else {
                                Log.d(TAG, "No such document");
                                cb.op(null);
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                            cb.op(null);
                        }
                    }
                }
                );
    }

    // assume email address is in a@b.c.d format
    // will return a@b-c-d
    // due to constrain of firestore
    private String cleanEmailStr(String email) {
        return email.replace('.','_');
    }

}