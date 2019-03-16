package com.android.personbest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;

import com.android.personbest.ChatBoxActivity;
import com.android.personbest.FriendshipManager.IChat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class MessagingServiceTest {
    private ChatBoxActivity chatBoxActivity;


    @Test
    public void testUserName(){
        Intent intent = new Intent(RuntimeEnvironment.application, ChatBoxActivity.class);
        intent.putExtra("chatId","test-atest-b");
        chatBoxActivity = Robolectric.buildActivity(ChatBoxActivity.class,intent).create().get();
        MockFireBase mockFireBase = new MockFireBase();
        mockFireBase.addObserver(chatBoxActivity);

        chatBoxActivity.setFireBase(mockFireBase);

        EditText nameView = chatBoxActivity.nameView;
        nameView.setText("DoubleR");
        chatBoxActivity.finish();
        intent = new Intent(RuntimeEnvironment.application,ChatBoxActivity.class);
        intent.putExtra("chatId","test-atest-b");
        chatBoxActivity = Robolectric.buildActivity(ChatBoxActivity.class,intent).create().get();
        SharedPreferences sharedpreferences = chatBoxActivity.getSharedPreferences("chats", Context.MODE_PRIVATE);
        String name = sharedpreferences.getString(chatBoxActivity.FROM_KEY, null);
        assertEquals(name, "DoubleR");
    }

    @Test
    public void testMessageOrder(){
        Intent intent = new Intent(RuntimeEnvironment.application,ChatBoxActivity.class);
        intent.putExtra("chatId","test-atest-b");
        chatBoxActivity = Robolectric.buildActivity(ChatBoxActivity.class,intent).create().get();
        MockFireBase mockFireBase = new MockFireBase();
        mockFireBase.addObserver(chatBoxActivity);

        chatBoxActivity.setFireBase(mockFireBase);
        chatBoxActivity.nameView.setText("Rubs");
        EditText messageView = chatBoxActivity.getMessageView();
        messageView.setText("Hello");
        Button send = chatBoxActivity.getSendButton();
        send.performClick();
        assertEquals(messageView.getText().toString(),"");
        messageView.setText("How are you?");
        send.performClick();
        assertEquals(messageView.getText().toString(),"");

    }


    class MockFireBase extends Observable implements IChat {
        public List<Map<String,String>> history;
        public boolean subscribed;

        public MockFireBase(){
            this.history = new ArrayList<>();
        }

        @Override
        public boolean add(Map<String, String> message) {
            this.history.add(message);
            setChanged();
            notifyObservers("");
            return true;
        }

        @Override
        public StringBuilder orderBy(){
            StringBuilder sb = new StringBuilder();
            for (Map<String,String> map: history){
                sb.append(map.get(chatBoxActivity.FROM_KEY));
                sb.append(":\n");
                sb.append(map.get(chatBoxActivity.TEXT_KEY));
                sb.append("\n");
                sb.append("---\n");
            }
            setChanged();
            notifyObservers(sb.toString());
            return sb;
        }

        @Override
        public void subscribeToTopic(String topic) {
            return;
        }

    }

}
