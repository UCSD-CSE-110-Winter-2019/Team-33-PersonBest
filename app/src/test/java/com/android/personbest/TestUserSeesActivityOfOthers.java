package com.android.personbest;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import com.android.personbest.FriendshipManager.FriendFireBaseAdapter;
import com.android.personbest.FriendshipManager.FriendshipManager;
import com.android.personbest.FriendshipManager.MockFirebaseAdapter;
import com.android.personbest.FriendshipManager.MockRelations;
import com.android.personbest.SavedDataManager.SavedDataManager;
import com.android.personbest.SavedDataManager.SavedDataManagerMock;
import com.android.personbest.SavedDataManager.SavedDataManagerSharedPreference;
import com.android.personbest.StepCounter.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class TestUserSeesActivityOfOthers {
    private FriendListActivity activity;

    private SavedDataManagerMock sd;
    private FriendshipManager fm;
    private MockFirebaseAdapter mfa;
    private String userId = "theUser";
    private String userIdFriend = "theFriend";

    @Before
    public void setup() {
        ExecMode.setExecMode(ExecMode.EMode.TEST_CLOUD);

        List<IStatistics> data = new ArrayList<>();
        List<IStatistics> dataFriend = new ArrayList<>();
        dataFriend.add(new DailyStat(10000,200,100,100, (float) 0.5));

        sd = new SavedDataManagerMock(data, dataFriend, userIdFriend, userIdFriend);

        Intent intent = new Intent(RuntimeEnvironment.application, FriendListActivity.class);
        mfa = new MockFirebaseAdapter();
        mfa.addFriendById(userIdFriend, userIdFriend);
        intent.putExtra("FFireBaseAdapter", mfa);
        intent.putExtra("id", this.userId);
        intent.putExtra("SavedDataManager", this.sd);
        activity = Robolectric.buildActivity(FriendListActivity.class, intent).create().get();

    }

    /**
     * Given the user has friend A
     *   And the user is in friendlist activity
     *   And A has goal 10000, walked 200 steps, 100 intentional steps, and walked for 100 ms with avg mph 0.5
     * When the user click on A
     *   And the user clicks on See Activity
     * Then FriendProgress Activity is launched
     *   And it shows a chart with empty information
     */
    @Test
    public void testBDD() {
    }
}
