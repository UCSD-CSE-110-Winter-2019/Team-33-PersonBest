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
import com.android.personbest.StepCounter.IStatistics;
import com.android.personbest.StepCounter.StepCounter;
import com.android.personbest.StepCounter.StepCounterFactory;
import com.android.personbest.StepCounter.StepCounterGoogleFit;
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
public class StoryTestUserSeesActivityOfOthers {
    private FriendListActivity activity;

    private SavedDataManagerMock sd;
    private FriendshipManager fm;
    private MockFirebaseAdapter mfa;
    private String userId = "theUser";
    private String userIdFriend = "theFriend";

    @Before
    public void setup() {
        ExecMode.setExecMode(ExecMode.EMode.TEST_CLOUD);

        sd = new SavedDataManagerMock(data, dataFriend);
        sd.setUserHeight(72, null, null);

        Intent intent = new Intent(RuntimeEnvironment.application, FriendListActivity.class);
        mfa = new MockFirebaseAdapter();
        intent.putExtra("FFireBaseAdapter", mfa);
        intent.putExtra("id", this.userId);
        intent.putExtra("SavedDataManager", this.sd);
        activity = Robolectric.buildActivity(FriendListActivity.class, intent).create().get();

        List<IStatistics> data = new ArrayList<>();
        List<IStatistics> dataFriend = new ArrayList<>();

    }

    /**
     * Given the user has friend A
     *   And the user is in friendlist activity
     *   And A did not make any progress
     * When the user click on A
     *   And the user clicks on See Activity
     * Then FriendProgress Activity is launched
     *   And it shows a chart with empty information
     */
    @Test
    public void testBDD() {
        Intent secondaryIntent = new Intent(this.mainActivity, BefriendActivity.class);
        this.activity = Robolectric.buildActivity(BefriendActivity.class, secondaryIntent).create().get();

        EditText et = activity.findViewById(R.id.friendInput);
        et.setText("Holmes");
        et = activity.findViewById(R.id.emailInput);
        et.setText("holmes@gmail.com");
        Button button = activity.findViewById(R.id.connect);

        activity.setRelations(new MockRelations());
        MockRelations relations = (MockRelations)activity.getRelations();
        MockFirebaseAdapter fba = relations.getFba();

        button.performClick();
        assertEquals("holmes@gmail.com", fba.getDb().get("Holmes"));
    }
}
