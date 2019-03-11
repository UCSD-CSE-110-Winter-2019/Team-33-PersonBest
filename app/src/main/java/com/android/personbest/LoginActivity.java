package com.android.personbest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.android.personbest.SavedDataManager.SavedDataManagerSharedPreference;
import com.android.personbest.SavedDataManager.SavedDataOperatorString;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_CONTACTS;

public class LoginActivity extends AppCompatActivity {

    private static final int GOAL_INIT = 0;
    private static final int DEFAULT_STEPS = 0;
    private static final int DEFAULT_GOAL = 5000;
    private static final float DEFAULT_MPH = 0;
    private static final Long DEFAULT_TIME = 0L;

    private static final int ERROR_STEPS = -1;
    private static final int ERROR_GOAL = -1;
    private static final float ERROR_MPH = -1;
    private static final Long ERROR_TIME = -1L;

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

    private static final int REQUEST_READ_CONTACTS = 0;
    private static final int RC_SIGN_IN = 77;
    private static final String TAG = "LoginActivity";

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private String curUsrID;
    private SavedDataManagerSharedPreference sdsp;

    CollectionReference ffUserData;
    DocumentReference ffCurUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        mAuth = FirebaseAuth.getInstance();
        sdsp = new SavedDataManagerSharedPreference(this);

        if(account != null) {
            goToMain();
        } else {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.web_client_id))
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            SignInButton signInButton = findViewById(R.id.sign_in_button);
            signInButton.setSize(SignInButton.SIZE_STANDARD);
            findViewById(R.id.sign_in_button).setOnClickListener(v -> signIn());
        }
    }

    private void signIn() {
        findViewById(R.id.sign_in_progress).setVisibility(View.VISIBLE);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.w(TAG, "on activity result!!!");//DEBUG

        if (requestCode == RC_SIGN_IN) {
            Log.w(TAG, "request code match!!!");//DEBUG
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.w(TAG, "get the account!!!");//DEBUG
                if(account != null) firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle: " + acct.getId());
        Log.d(TAG, "firebaseAuthWithGoogleIdToken: " + acct.getIdToken());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            curUsrID = user.getUid();

                            FirebaseFirestore ff = FirebaseFirestore.getInstance();
                            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build();
                            ff.setFirestoreSettings(settings);

                            ffUserData = ff.collection("user_data");
                            ffCurUserData = ff.document("user_data/"+String.valueOf(curUsrID));

                            initUserData(acct, user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    private void initUserData(GoogleSignInAccount acct,FirebaseUser user) {

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

                                // set current goal
                                sdsp.setCurrentGoal((int)(long)theGoal, null, null);

                                data.put(FIEKEY_CUR_GOAL, theGoal);
                            } else {
                                Log.d(TAG, "Initialize user-data for new user...");
                                sdsp.setCurrentGoal((int)(long)DEFAULT_GOAL, null, null);
                                data.put(FIEKEY_CUR_GOAL, sdsp.getCurrentGoal(null));
                            }
                            data.put(FIEKEY_EMAIL, acct.getEmail());
                            data.put(FIEKEY_HEIGHT, sdsp.getUserHeight(null));
                            ffCurUserData.set(data);
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }

                        goToMain(); // now we can quit sign in
                    }
                });
        // create the sub-collections here? -> No
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        this.finish();
        Log.w(TAG, "go to main!!!");//DEBUG
    }

    private String cleanEmailStr(String email) {
        return email.replace('.','_');
    }
}

