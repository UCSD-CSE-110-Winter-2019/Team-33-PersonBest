package com.android.personbest.FriendshipManager;

import android.util.Log;

import java.util.Map;

public interface IChat {
    boolean add(Map<String, String> message);
    StringBuilder orderBy();
    void subscribeToTopic(String topic);
}
