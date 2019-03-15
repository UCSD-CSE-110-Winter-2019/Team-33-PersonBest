package com.android.personbest;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.*;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatBoxActivity extends AppCompatActivity {
    String TAG = MainActivity.class.getSimpleName();

    String COLLECTION_KEY = "chats";
    String CHAT_ID;
    String MESSAGES_KEY = "messages";
    String FROM_KEY = "from";
    String TEXT_KEY = "text";
    String TIMESTAMP_KEY = "timestamp";

    CollectionReference chat;
    String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_chat_box);
        SharedPreferences sharedpreferences = getSharedPreferences("chats", Context.MODE_PRIVATE);
        from = sharedpreferences.getString(FROM_KEY, null);

        CHAT_ID = getIntent().getStringExtra("chatId");
        chat = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(CHAT_ID)
                .collection(MESSAGES_KEY);

        initMessageUpdateListener();

        findViewById(R.id.btn_send).setOnClickListener(view -> sendMessage());

        EditText nameView = findViewById((R.id.user_name));
        nameView.setText(from);
        nameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                from = s.toString();
                sharedpreferences.edit().putString(FROM_KEY, from).apply();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        this.subscribeToNotificationsTopic();
    }

    private void sendMessage() {
        if (from == null || from.isEmpty()) {
            Toast.makeText(this, "Enter your name", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText messageView = findViewById(R.id.text_message);

        Map<String, String> newMessage = new HashMap<>();
        newMessage.put(FROM_KEY, from);
        newMessage.put(TEXT_KEY, messageView.getText().toString());

        chat.add(newMessage).addOnSuccessListener(result -> {
            messageView.setText("");
        }).addOnFailureListener(error -> {
            Log.e(TAG, error.getLocalizedMessage());
        });
    }

    private void initMessageUpdateListener() {
        chat.orderBy(TIMESTAMP_KEY, Query.Direction.ASCENDING).addSnapshotListener((newChatSnapShot, error) -> {
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


                TextView chatView = findViewById(R.id.chat);
                chatView.append(sb.toString());
            }
        });
    }

    private void subscribeToNotificationsTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(CHAT_ID).addOnCompleteListener(task -> {
            String msg =  "Subscribed to notifications" ;  if  (!task.isSuccessful()) {
                msg =  "Subscribe to notifications failed" ; }
            Log.d(TAG, msg);
            Toast.makeText(ChatBoxActivity.this, msg, Toast.LENGTH_SHORT).show(); }
        );
    }
}
