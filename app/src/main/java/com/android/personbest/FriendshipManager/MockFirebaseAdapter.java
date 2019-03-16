package com.android.personbest.FriendshipManager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Observable;

public class MockFirebaseAdapter extends Observable implements FFireBaseAdapter, Serializable {
    HashMap<String, String> db;
    private String user;

    public MockFirebaseAdapter(String user) {
        this.user = user;
        this.db = new HashMap<>();
    }

    @Override
    public void addFriendById(String name, String id) {
        System.out.println("Adding New Friend:" + name + " " + id);
        this.db.put(name, id);
    }

    @Override
    public void getFriendlist() {
        System.out.println("Get Friend List");
        db.forEach((k,v)->{
            setChanged();
            notifyObservers(v + "_" + k);
        });
    }

    public HashMap<String, String> getDb() {
        return this.db;
    }

    public void hasFriend(OperatorBoolean b) {
        b.op(db.size() > 0);
    }

    public String generateIDChat(String idFriend) {
        String chatId = "";
        if (user.compareTo(idFriend)<0){
            chatId = user+idFriend;
        }
        else {
            chatId = idFriend+user;
        }

        return chatId;
    }

}
