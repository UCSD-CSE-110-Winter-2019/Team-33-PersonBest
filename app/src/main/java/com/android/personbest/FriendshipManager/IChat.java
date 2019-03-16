package com.android.personbest.FriendshipManager;

import java.util.Map;

public interface IChat {
    boolean add(Map<String, String> message);
    StringBuilder orderBy();
}
