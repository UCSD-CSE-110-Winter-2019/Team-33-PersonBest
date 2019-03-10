package com.android.personbest.SavedDataManager;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import com.android.personbest.StepCounter.DailyStat;
import com.android.personbest.StepCounter.IStatistics;
import com.android.personbest.Timer.ITimer;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: might want to replace all the default goals with current goal
public class SavedDataManagerFirestore implements SavedDataManager {

    private static final int GOAL_INIT = 0;
    private static final int DEFAULT_STEPS = 0;
    private static final int DEFAULT_GOAL = 5000;
    private static final float DEFAULT_MPH = 0;
    private static final Long DEFAULT_TIME = 0L;

    private static final int ERROR_STEPS = -1;
    private static final int ERROR_GOAL = -1;
    private static final float ERROR_MPH = -1;
    private static final Long ERROR_TIME = -1L;
    private static final String TAG = "SavedDataManagerFirestore";

    private static final String DOCKEY_HIST = "history";
    private static final String D_KEY_MPH = "avg-mph";
    private static final String D_KEY_EXER_TIME = "exercise-time";
    private static final String D_KEY_GOALS = "goal";
    private static final String D_KEY_INTE_STEPS = "intentional-steps";
    private static final String D_KEY_STEPS = "steps";

    private static final String DOCKEY_EMAILS = "emails";
    private static final String FIEKEY_EMAILMAP = "email_map";
    private static final String FIEKEY_CUR_GOAL = "current-goal";
    private static final String FIEKEY_EMAIL = "email";
    private static final String FIEKEY_HEIGHT = "height";
    private static final String KEY_VAL = "val";

    private static final String KEY_DAYSTAMP = "daystamp";

    private Activity activity;
    private SavedDataManagerSharedPreference sdsp;
    private FirebaseFirestore ff;
    private String curUsrID;

    private FirebaseAuth mAuth;
    private GoogleSignInAccount curAccount;
    private FirebaseUser curFirebaseUser;

    private CollectionReference ffUserData;
    private DocumentReference ffCurUserData;
    private CollectionReference ffCurUserDataHist;

    public SavedDataManagerFirestore(Activity activity) {
        this.activity = activity;
        this.sdsp = new SavedDataManagerSharedPreference(activity);
        this.ff = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        curAccount = GoogleSignIn.getLastSignedInAccount(activity);


        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        ff.setFirestoreSettings(settings);
        curUsrID = curAccount.getId();
        ffUserData = ff.collection("user_data");
        ffCurUserData = ff.document("user_data/"+String.valueOf(curUsrID));
        ffCurUserDataHist = ff.document("user_data/"+String.valueOf(curUsrID)).collection(DOCKEY_HIST);

        // check firebase auth *should be* enough to check if the user used this app before
        // but **failed** to do so even in development
        curFirebaseUser = mAuth.getCurrentUser();
        if (curFirebaseUser == null) {
            firebaseAuthWithGoogle(curAccount);
            curFirebaseUser = mAuth.getCurrentUser();
            initUserData(curAccount);
        }
    }

    // online lookup
    // user is downloading the whole map data
    // TODO: optimize this
    public void getIdByEmail(String email, SavedDataOperatorString callback) {
        final String query = cleanEmailStr(email);
        final SavedDataOperatorString cb = callback;
        ffUserData.document(DOCKEY_EMAILS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                HashMap<String, String> ma = (HashMap<String, String>) document.getData().get(FIEKEY_EMAILMAP);
                                Log.d(TAG, "DocumentSnapshot data: " + ma);
                                if(ma.containsKey(query)){
                                    cb.op(ma.get(query));
                                } else {
                                    cb.op(null);
                                }
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

    // height is updated

    public int getUserHeight(SavedDataOperatorInt callback) {
        if(callback != null) {
            final SavedDataOperatorInt cb = callback;
            ffCurUserData.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Integer height = (Integer) document.getData().get(FIEKEY_HEIGHT);
                                if(height == null) height = 0;
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
        Map<String, Object> data = new HashMap<>();
        data.put(FIEKEY_HEIGHT, height);
        ffCurUserData.update(data);
        sdsp.setUserHeight(height,null,null);
    }

    public int getCurrentGoal(SavedDataOperatorInt callback) {
        if(callback != null) {
            final SavedDataOperatorInt cb = callback;
            ffCurUserData.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Long va = (Long) document.getData().get(FIEKEY_CUR_GOAL);
                                if(va == null) va = (long) DEFAULT_GOAL;
                                Integer val = (int) (long) va;
                                Log.d(TAG, "DocumentSnapshot data: " + val);
                                cb.op(val);
                            } else {
                                Log.d(TAG, "No such document");
                                cb.op(DEFAULT_GOAL);
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                            cb.op(-1);
                        }
                    }
                });
        }
        return sdsp.getCurrentGoal(null);
    }
    public void setCurrentGoal(int goal, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp) {
        Map<String, Object> data = new HashMap<>();
        data.put(FIEKEY_CUR_GOAL, goal);
        ffCurUserData.update(data);
        sdsp.setCurrentGoal(goal, null, null);
    }

    // below will use set
    // to overwrite

    // and will return default/current value if not exist

    public int getStepsByDayStr(String day, SavedDataOperatorInt callback) {
        getData(D_KEY_STEPS, DEFAULT_STEPS, day, callback);
        return sdsp.getStepsByDayStr(day,null);
    }
    public void setStepsByDayStr(String day, int step, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp) {
        setData(D_KEY_STEPS, day, step, onSuccessStrOp, onFailureStrOp);
        sdsp.setStepsByDayStr(day, step, null, null);
    }

    public int getIntentionalStepsByDayStr(String day, SavedDataOperatorInt callback) {
        getData(D_KEY_INTE_STEPS, DEFAULT_STEPS, day, callback);
        return sdsp.getIntentionalStepsByDayStr(day, null);
    }
    public void setIntentionalStepsByDayStr(String day, int step, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp) {
        setData(D_KEY_INTE_STEPS, day, step, onSuccessStrOp, onFailureStrOp);
        sdsp.setIntentionalStepsByDayStr(day, step, null, null);
    }

    public long getExerciseTimeByDayStr(String day, SavedDataOperatorLong callback) {
        getData(D_KEY_EXER_TIME, DEFAULT_TIME, day, callback);
        return sdsp.getExerciseTimeByDayStr(day, null);
    }

    public void setExerciseTimeByDayStr(String day, long time, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp) {
        setData(D_KEY_EXER_TIME, day, time, onSuccessStrOp, onFailureStrOp);
        sdsp.setExerciseTimeByDayStr(day, time, null, null);
    }

    public float getAvgMPHByDayStr(String day, SavedDataOperatorFloat callback) {
        getData(D_KEY_MPH, DEFAULT_MPH, day, callback);
        return sdsp.getAvgMPHByDayStr(day, null);
    }
    public void setAvgMPHByDayStr(String day, float mph, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp) {
        setData(D_KEY_MPH, day, mph, onSuccessStrOp, onFailureStrOp);
        sdsp.setAvgMPHByDayStr(day, mph, null, null);
    }


    // if today not in db, use current goal
    public int getGoalByDayStr(String day, SavedDataOperatorInt callback) {
        // if today not in db, use current goal
        getData(D_KEY_GOALS, DEFAULT_GOAL, day, callback);
        return sdsp.getGoalByDayStr(day, null);
    }
    public void setGoalByDayStr(String day, int goal, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp) {
        setData(D_KEY_GOALS, day, goal, onSuccessStrOp, onFailureStrOp);
        sdsp.setGoalByDayStr(day, goal, null, null);
    }

    // clear everything
    public void clearData() {
        // do nothing
    }

    // note: if not exist, will use default
    // might need to change in the future
    public IStatistics getStatByDayStr(String day, SavedDataOperatorIStat callback) {
        if(callback != null) {
            final SavedDataOperatorIStat cb = callback;
            ffCurUserDataHist.document(day).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Map<String, Object> val = document.getData();
                                    Log.d(TAG, "DocumentSnapshot data: " + val);

                                    cb.op(createIstatFromMap(val));
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
        return sdsp.getStatByDayStr(day,null);
    }

    public List<IStatistics> getLastWeekSteps(String day, SavedDataOperatorListIStat callback) {
        getDataBatchIstat(ffCurUserDataHist, ITimer.getDayStampWeekBefore(day), callback);
        return sdsp.getLastWeekSteps(day,null);
    }
    public List<IStatistics> getLastMonthStat(String day, SavedDataOperatorListIStat callback) {
        getDataBatchIstat(ffCurUserDataHist, ITimer.getDayStampMonthBefore(day), callback);
        return sdsp.getLastMonthStat(day,null);
    }

    public List<IStatistics> getFriendMonthlyStat(String email, String day, SavedDataOperatorListIStat callback) {
        getIdByEmail(email, id -> {
            getDataBatchIstat(ffCurUserData.collection(id), ITimer.getDayStampMonthBefore(day), callback);
        });
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
        Log.d(TAG, "firebaseAuthWithGoogle: " + acct.getId());
        Log.d(TAG, "firebaseAuthWithGoogleIdToken: " + acct.getIdToken());

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

    private void initUserData(GoogleSignInAccount acct) {
        curUsrID = acct.getId();

        // will fail if "email_map" document not exist
        // will fail if the email has '.' char in it
        ffUserData.document("emails")
                .update(
                        "email_map."+cleanEmailStr(acct.getEmail()), acct.getId()
                );

        // will initialize user in user_data if not pre-exist
        ffCurUserData.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Map<String, Object> data = new HashMap<>();
                            // user is FREE TO SET THE NEW HEIGHT
                            // BUT THE GOAL WILL BE PRESERVED
                            Object theGoal = document.get(FIEKEY_CUR_GOAL);
                            if (document.exists() && theGoal != null) {
                                // current goal should always exist if the document exists
                                // don't know if check if necessary
                                Log.d(TAG, "Retrieving GOAL of old user...");
                                setCurrentGoal((int)(long)theGoal, null, null);
                                data.put(FIEKEY_CUR_GOAL, theGoal);
                            } else {
                                Log.d(TAG, "Initialize user-data for new user...");
                                data.put(FIEKEY_CUR_GOAL, sdsp.getCurrentGoal(null));
                            }
                            data.put(FIEKEY_EMAIL, acct.getEmail());
                            data.put(FIEKEY_HEIGHT, sdsp.getUserHeight(null));
                            ffCurUserData.set(data);
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });


        // create the sub-collections here? -> No
    }

    // assume email address is in a@b.c.d format
    // will return a@b-c-d
    // due to constrain of firestore
    private String cleanEmailStr(String email) {
        return email.replace('.','_');
    }

    ////////////////////////////////////////////////////////////////////


    private void getData(final String col_key, final int default_val, String day, SavedDataOperatorInt callback) {
        if(callback == null) return;
        final SavedDataOperatorInt cb = callback;
        ffCurUserDataHist.document(day).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Long va = (Long) document.getData().get(col_key);
                                if(va == null) va = (long) default_val;
                                Integer val = (int) (long) va;
                                Log.d(TAG, "DocumentSnapshot data: " + val);
                                cb.op(val);
                            } else {
                                Log.d(TAG, "No such document");
                                cb.op(default_val);
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                            cb.op(-1);
                        }
                    }
                });
    }

    private void getData(final String col_key, final long default_val, String day, SavedDataOperatorLong callback) {
        if(callback == null) return;
        final SavedDataOperatorLong cb = callback;
        ffCurUserDataHist.document(day).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Long val = (Long) document.getData().get(col_key);
                                if(val == null) val = default_val;
                                Log.d(TAG, "DocumentSnapshot data: " + val);
                                cb.op(val);
                            } else {
                                Log.d(TAG, "No such document");
                                cb.op(default_val);
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                            cb.op(-1L);
                        }
                    }
                });
    }

    private void getData(final String col_key, final float default_val, String day, SavedDataOperatorFloat callback) {
        if(callback == null) return;
        final SavedDataOperatorFloat cb = callback;
        ffCurUserDataHist.document(day).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Double val = (Double) document.getData().get(col_key);
                                if(val == null) val = (double) default_val;
                                float va = (float) (double) val;
                                Log.d(TAG, "DocumentSnapshot data: " + val);
                                cb.op(va);
                            } else {
                                Log.d(TAG, "No such document");
                                cb.op(default_val);
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                            cb.op(-1);
                        }
                    }
                });
    }

    private void getDataBatchIstat(CollectionReference col, int dayStamp, SavedDataOperatorListIStat callback) {
        if(col != null && callback != null) {
            final SavedDataOperatorListIStat cb = callback;
            Query query = col.orderBy(KEY_DAYSTAMP).whereGreaterThanOrEqualTo(KEY_DAYSTAMP, dayStamp);
            query.get()
                 .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                     @Override
                     public void onComplete(@NonNull Task<QuerySnapshot> task) {
                         if (task.isSuccessful()) {
                             ArrayList<IStatistics> toReturn = new ArrayList<>();

                             DocumentReference doc;
                             for (QueryDocumentSnapshot document : task.getResult()) {

                                 Map<String, Object> val = document.getData();
                                 Log.d(TAG, "QueryDocumentSnapshot data: " + val);

                                 toReturn.add(createIstatFromMap(val));
                             }

                             cb.op(toReturn);
                         } else {
                             Log.d(TAG, "get failed with ", task.getException());
                             cb.op(null);
                         }
                     }
                 });
        }
    }

    private IStatistics createIstatFromMap(Map<String, Object> val) {
        int goal = val.containsKey(D_KEY_GOALS) ? (int) (long) val.get(D_KEY_GOALS) : DEFAULT_GOAL;
        int totalSteps = val.containsKey(D_KEY_STEPS) ? (int) (long) val.get(D_KEY_STEPS) : DEFAULT_STEPS;
        int intentionalSteps = val.containsKey(D_KEY_INTE_STEPS) ? (int) (long) val.get(D_KEY_INTE_STEPS) : DEFAULT_STEPS;
        Float MPH = val.containsKey(D_KEY_MPH) ? (float) (double) val.get(D_KEY_MPH) : DEFAULT_MPH;
        Long timeWalked = val.containsKey(D_KEY_EXER_TIME) ? (long) val.get(D_KEY_EXER_TIME) : DEFAULT_TIME;
        Log.d(TAG, "Creating a IStat object: " + goal + " " + totalSteps + " " + intentionalSteps + " " + timeWalked + " " + MPH);
        return (new DailyStat(goal, totalSteps, intentionalSteps, timeWalked, MPH));
    }

    private void setData(final String col_key, String day, Object val, SavedDataOperatorString onSuccessStrOp, SavedDataOperatorString onFailureStrOp) {
        final SavedDataOperatorString suc = onSuccessStrOp, fai = onFailureStrOp;
        final Object fval = val; final String fday = day;

        Map<String, Object> data = new HashMap<>();
        data.put(KEY_DAYSTAMP, ITimer.getDayStamp(day));
        data.put(col_key, val);
        ffCurUserDataHist.document(day)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Document successfully written for day: " + day);
                        if(suc != null) {
                            suc.op("[SUCCEED] " + col_key + " : " + fday + " : " + String.valueOf(fval));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        if(fai != null) {
                            fai.op("[FAILED] " + col_key + " : " + fday + " : " + String.valueOf(fval));
                        }
                    }
                });
    }
}
