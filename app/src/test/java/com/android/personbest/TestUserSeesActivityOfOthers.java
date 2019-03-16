package com.android.personbest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowIntent;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
public class TestUserSeesActivityOfOthers {
    private FriendListActivity activity;
    private ShadowActivity shadowFriendListActivity;
    private ShadowActivity shadowFriendProgressActivity;

    private SavedDataManagerMock sd;
    private FriendshipManager fm;
    private MockFirebaseAdapter mfa;
    private String userId = "theUser";
    private String userIdFriend = "theFriend";

    @Before
    public void setup() {
        ExecMode.setExecMode(ExecMode.EMode.TEST_LOCAL);

        List<IStatistics> data = new ArrayList<>();
        List<IStatistics> dataFriend = new ArrayList<>();
        dataFriend.add(new DailyStat(10000,200,100,100, (float) 0.5));

        sd = new SavedDataManagerMock(data, dataFriend, userIdFriend, userIdFriend);

        Intent intent = new Intent(RuntimeEnvironment.application, FriendListActivity.class);
        mfa = new MockFirebaseAdapter(userId);
        mfa.addFriendById(userIdFriend, userIdFriend);
        intent.putExtra("FFireBaseAdapter", mfa);
        intent.putExtra("id", this.userId);
        intent.putExtra("SavedDataManager", this.sd);
        try {
            activity = Robolectric.buildActivity(FriendListActivity.class, intent).create().get();
        } catch(Exception e) {
            activity = Robolectric.buildActivity(FriendListActivity.class, intent).create().get();
        }
        shadowFriendListActivity = Shadows.shadowOf(activity);
    }

    /**
     * Given the user has friend A
     *   And the user is in friendlist activity
     *   And A has goal 10000, walked 200 steps, 100 intentional steps, and walked for 100 ms with avg mph 0.5
     * When the user click on A
     *   And the user clicks on See Activity
     * Then A chart of friend's Activity is shown in the screen
     */
    @Test
    public void testSeeActivity() {
        System.out.println("************Begin to test our BDD**************");
        ListView friendListView = (ListView) activity.getListView();
        Shadows.shadowOf(friendListView).performItemClick(0); // press one and only one friend
        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertNotNull(alertDialog);

        Button confirm = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        confirm.performClick();

        Intent newIntent = Shadows.shadowOf(activity).getNextStartedActivity();
        ShadowIntent shadowIntent = Shadows.shadowOf(newIntent);
        assertEquals(FriendProgress.class, shadowIntent.getIntentClass());
    }
}
