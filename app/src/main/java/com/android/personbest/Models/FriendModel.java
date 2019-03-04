package com.android.personbest.Models;

public class FriendModel {
    private String name = "";

    public FriendModel() {
        // needed for firebase
    }

    public FriendModel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
