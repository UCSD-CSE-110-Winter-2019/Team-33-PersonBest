package com.android.personbest;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.personbest.FriendshipManager.FirebaseMessagingAdapter;
import com.android.personbest.FriendshipManager.IChat;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.*;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class ChatBoxActivity extends AppCompatActivity implements Observer {
    String TAG = MainActivity.class.getSimpleName();

    String COLLECTION_KEY = "chats";
    String CHAT_ID;
    String MESSAGES_KEY = "messages";
    public String FROM_KEY = "from";
    public String TEXT_KEY = "text";
    String TIMESTAMP_KEY = "timestamp";
    public EditText nameView;

    CollectionReference chat;
    String from;

    IChat iChat;

    public void update(Observable o, Object arg){
        String msg = (String) arg;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (msg.length() == 0) {
                    EditText messageView = findViewById(R.id.text_message);
                    messageView.setText(arg.toString());
                }
                else if (msg.equals("Success")){
                    String msg = "Subscribed to notifications";
                    Log.d(TAG, msg);
                    Toast.makeText(ChatBoxActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
                else if (msg.equals("Fail")){
                    String msg =  "Subscribe to notifications failed";
                    Log.d(TAG, msg);
                    Toast.makeText(ChatBoxActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
                else{
                    TextView chatView = findViewById(R.id.chat);
                    chatView.append(msg);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_chat_box);
        SharedPreferences sharedpreferences = getSharedPreferences("chats", Context.MODE_PRIVATE);
        from = sharedpreferences.getString(FROM_KEY, null);

        CHAT_ID = getIntent().getStringExtra("chatId");

        iChat = new FirebaseMessagingAdapter(CHAT_ID);
        ((FirebaseMessagingAdapter)iChat).addObserver(this);

        initMessageUpdateListener();

        findViewById(R.id.btn_send).setOnClickListener(view -> sendMessage());

        nameView = findViewById((R.id.user_name));
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

        boolean result = iChat.add(newMessage);

    }

    public EditText getMessageView(){
        return findViewById(R.id.text_message);
    }

    public Button getSendButton(){
        return findViewById(R.id.btn_send);
    }

    public void setFireBase(IChat iChat){
        this.iChat = iChat;
    }

    private void initMessageUpdateListener() {
        /*chat.orderBy(TIMESTAMP_KEY, Query.Direction.ASCENDING).addSnapshotListener((newChatSnapShot, error) -> {
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
        });*/


        StringBuilder result = this.iChat.orderBy();
        if (result != null){
            TextView chatView = findViewById(R.id.chat);
            chatView.append(result.toString());
        }
    }
}
