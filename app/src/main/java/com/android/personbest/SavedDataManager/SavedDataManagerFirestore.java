package com.android.personbest.SavedDataManager;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import com.android.personbest.StepCounter.IStatistics;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.List;

public class SavedDataManagerFirestore implements SavedDataManager {
    private static final int GOAL_INIT = 0;
    private final int DEFAULT_STEPS = 0;
    private final int DEFAULT_GOAL = 5000;
    private final float DEFAULT_MPH = 0;
    private final Long DEFAULT_TIME = 0L;
    private final String TAG = "SavedDataManagerFirestore";

    private Activity activity;
    private SavedDataManagerSharedPreference sdsp;
    private FirebaseFirestore ff;
    private String curUsrID;

    private FirebaseAuth mAuth;
    private GoogleSignInAccount curAccount;
    private FirebaseUser curFirebaseUser;

    public SavedDataManagerFirestore(Activity activity) {
        this.activity = activity;
        this.sdsp = new SavedDataManagerSharedPreference(activity);
        this.ff = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        curAccount = GoogleSignIn.getLastSignedInAccount(activity);
        curFirebaseUser = mAuth.getCurrentUser();
        if (curFirebaseUser == null) {
            firebaseAuthWithGoogle(curAccount);
            curFirebaseUser = mAuth.getCurrentUser();
        }

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        ff.setFirestoreSettings(settings);

        curUsrID = curAccount.getId();
    }

    // online lookup
    public void getIdByEmail(String email, SavedDataOperatorString callback) {
        final String query = email;
        final SavedDataOperatorString cb = callback;
        ff.collection("user_data")
                .document("emails")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String ans = (String) document.getData().get(query);
                                Log.d(TAG, "DocumentSnapshot data: " + ans);
                                cb.op(ans);
                            } else {
                                Log.d(TAG, "No such document");
                                cb.op(null);
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                            cb.op(null);
                        }
                    }
                });
    }

    // data to sync

    // lambda function uses data in the cloud, discard return value directly
    public int getUserHeight(SavedDataOperatorInt callback) {
        if(callback != null) {
            final SavedDataOperatorInt cb = callback;
            ff.collection("user_data")
                .document(curUsrID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Integer height = (Integer) document.getData().get("height");
                                Log.d(TAG, "DocumentSnapshot data: " + height);
                                cb.op(height);
                            } else {
                                Log.d(TAG, "No such document");
                                cb.op(-1);
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                            cb.op(-1);
                        }
                    }
                });
        }
        return sdsp.getUserHeight(null);
    }
    public void setUserHeight(int height, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp) {
    }

    public int getStepsByDayStr(String day, SavedDataOperatorInt callback) {
        return 0;
    }
    public void setStepsByDayStr(String day, int step, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp) {
    }

    public int getIntentionalStepsByDayStr(String day, SavedDataOperatorInt callback) {
        return 0;
    }
    public void setIntentionalStepsByDayStr(String day, int step, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp) {
    }

    public long getExerciseTimeByDayStr(String day, SavedDataOperatorLong callback) {
        return 0;
    }

    public void setExerciseTimeByDayStr(String day, long time, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp) {
    }

    public float getAvgMPHByDayStr(String day, SavedDataOperatorFloat callback) {
        return 0;
    }
    public void setAvgMPHByDayStr(String day, float mph, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp) {
    }

    public int getCurrentGoal(SavedDataOperatorInt callback) {
        return 0;
    }
    public void setCurrentGoal(int goal, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp) {
    }

    // if today not in db, use current goal
    public int getGoalByDayStr(String day, SavedDataOperatorInt callback) {
        // if today not in db, use current goal
        return 0;
    }
    public void setGoalByDayStr(String day, int goal, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp) {
    }

    // clear everything
    public void clearData() {

    }

    // note: if not exist, will use default
    // might need to change in the future
    public IStatistics getStatByDayStr(String day, SavedDataOperatorIStat callback) {
        return null;
//        int totalSteps = this.getStepsByDayStr(day);
//        int goal = this.getStepsByDayStr(day);
//
//        int intentionalSteps =
//        Float MPH =
//        Long timeWalked =
//
//        return new DailyStat(goal,totalSteps,intentionalSteps,timeWalked,MPH);
    }

    public List<IStatistics> getLastWeekSteps(String day, SavedDataOperatorListIStat callback) {
        return null;
    }
    public List<IStatistics> getLastMonthStat(String day, SavedDataOperatorListIStat callback) {
        return null;
    }

    public List<IStatistics> getFriendMonthlyStat(String email, SavedDataOperatorListIStat callback) {
        return null;
    }

    ////////////////////////////////////////////////////////////////////////

    public boolean isFirstTimeUser() {
        return sdsp.isFirstTimeUser();
    }
    public void setFirstTimeUser(boolean isFirstTime) {
        sdsp.setFirstTimeUser(isFirstTime);
    }

    public boolean isShownGoal(String today) {
        return sdsp.isShownGoal(today);
    }
    public void setShownGoal(String today){
        sdsp.setShownGoal(today);
    }

    public boolean isShownSubGoal(String today){
        return sdsp.isShownSubGoal(today);
    }
    public void setShownSubGoal(String today){
        sdsp.setShownSubGoal(today);
    }

    public boolean isCheckedYesterdayGoal(String today) {
        return sdsp.isCheckedYesterdayGoal(today);
    }

    public void setCheckedYesterdayGoal(String today) {
        sdsp.setCheckedYesterdayGoal(today);
    }

    public boolean isShownYesterdayGoal(String today) {
        return sdsp.isShownYesterdayGoal(today);
    }
    public void setShownYesterdayGoal(String today) {
        sdsp.setShownYesterdayGoal(today);
    }

    public boolean isCheckedYesterdaySubGoal(String today) {
        return sdsp.isCheckedYesterdaySubGoal(today);
    }

    public void setCheckedYesterdaySubGoal(String today) {
        sdsp.setCheckedYesterdaySubGoal(today);
    }

    public boolean isShownYesterdaySubGoal(String today) {
        return sdsp.isShownYesterdaySubGoal(today);
    }
    public void setShownYesterdaySubGoal(String today) {
        sdsp.setShownYesterdaySubGoal(today);
    }

    //////////////////////////////////////////////////////////////////////

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //updateUI(null);
                        }
                    }
                });
    }
}
