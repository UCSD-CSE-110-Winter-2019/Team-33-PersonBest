package com.android.personbest.FriendshipManager;

import com.android.personbest.SavedDataManager.SavedDataOperatorString;

public interface Email2Id {
    void getIdByEmail(String email, SavedDataOperatorString callback);
}
