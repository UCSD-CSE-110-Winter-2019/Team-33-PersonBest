package com.android.personbest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.personbest.FriendshipManager.*;
import com.android.personbest.SavedDataManager.SavedDataManager;
import com.android.personbest.SavedDataManager.SavedDataManagerFirestore;
import com.android.personbest.SavedDataManager.SavedDataManagerSharedPreference;
import com.android.personbest.StepCounter.*;
import com.android.personbest.Timer.ITimer;
import com.android.personbest.Timer.TimerSystem;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.*;

public class MainActivity extends AppCompatActivity implements Observer {

    private static final int GOAL_INIT = 5000; // default
    private static final int STEP_INIT = 0;
    private static final long MILLISECONDS_IN_A_MINUTE = 60000;
    private static final long MILLISECONDS_IN_A_SECOND = 1000;

    // Const static member
    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";
    private static final String TAG = "MainActivity";
    private static final String TEST_CUR_USR_ID = "test-uid";

    private static ExecMode.EMode test_mode;

    // private variables
    private String fitnessServiceKey;
    private int goalNum;
    private boolean plannedExercise = false;
    private long timer;
    private int plannedSteps;
    private int totalIntentionalSteps;
    private StepCounterGoogleFit stepCounter;
    private IntentionalWalkUtils intentionalWalkUtils = new IntentionalWalkUtils();
    private SavedDataManager sd;
    private Relations friendshipManager;
    private FFireBaseAdapter fFireBaseAdapter;
    private SharedPreferences sp;
    private ITimer theTimer;
    private ProgressEncouragement progressEncouragement;
    private IDate theDate;
    private String today;
    private Integer todayInt;
    private String userId;
    private Boolean hasFriend;

//    private FirebaseAuth mAuth;
//    private GoogleSignInAccount curAccount;
//    private FirebaseUser curFirebaseUser;

    private boolean NDEBUG = true;

    // UI-related members

    private TextView stepsTodayVal;
    private TextView goalVal;
    private TextView stepsLeftVal;
    private TextView columns;
    private TextView plannedTimeValue;
    private TextView plannedStepValue;
    private TextView plannedMPHValue;
    private ProgressBar progressBar;
    private Button addFriend;
    private Button viewFriends;

    public void update(Observable o, Object arg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stepCounter.updateStepCount();
                int totalSoFar = Integer.parseInt(stepsTodayVal.getText().toString());
                sd.setStepsByDayStr(theTimer.getTodayString(),totalSoFar, null, null);
                if(plannedTimeValue.getVisibility() == View.VISIBLE) {
                    long timeDiff = (System.currentTimeMillis() - timer);
                    plannedTimeValue.setText(String.valueOf(timeDiff / MILLISECONDS_IN_A_MINUTE));

                    int stepDiff = totalSoFar - plannedSteps;
                    plannedStepValue.setText(String.valueOf(stepDiff));

                    double currMph = intentionalWalkUtils.velocity(sd.getUserHeight(null), stepDiff, timeDiff / MILLISECONDS_IN_A_SECOND);
                    plannedMPHValue.setText(String.valueOf(currMph));
                }
                int left = goalNum - totalSoFar;
                if(left > 0) stepsLeftVal.setText(String.valueOf(left));
                else stepsLeftVal.setText("0");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Activity self = this;

        FirebaseApp.initializeApp(this);

        // Setup UI
        stepsTodayVal = findViewById(R.id.stepsTodayVal);
        goalVal = findViewById(R.id.goalVal);
        stepsLeftVal = findViewById(R.id.stepsLeftVal);
        columns = findViewById(R.id.plannedColumns);
        plannedTimeValue = findViewById(R.id.timeValue);
        plannedStepValue = findViewById(R.id.stepValue);
        plannedMPHValue = findViewById(R.id.mphValue);
        setPlannedExerciseStatsVisibility(false);

        stepsTodayVal.setText(String.valueOf(STEP_INIT));
        stepsLeftVal.setText(String.valueOf(goalNum - STEP_INIT));

        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(GOAL_INIT);
        progressBar.setMin(0);
        progressBar.setProgress(0);


        // we testing?
        test_mode = ExecMode.getExecMode();
        if(test_mode == ExecMode.EMode.TEST_CLOUD) {
            sd = new SavedDataManagerSharedPreference(this); // TODO a mock firestore adapter
        } else if (test_mode == ExecMode.EMode.TEST_LOCAL) {
            sd = new SavedDataManagerSharedPreference(this);
        }
        else {
            // set saved data manager
            sd = new SavedDataManagerFirestore(this);
        }


        theTimer = new TimerSystem();
        progressEncouragement = new ProgressEncouragement(this);
        theDate = new DateCalendar();

        final Button startStopBtn = findViewById(R.id.startStop);
        startStopBtn.setText("  Start Walk/Run  ");
        startStopBtn.setTextColor(Color.BLACK);
        startStopBtn.setBackgroundColor(Color.GREEN);
        startStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plannedExercise = !plannedExercise;
                if(plannedExercise) {
                    startStopBtn.setText("    End Walk/Run    ");
                    startStopBtn.setBackgroundColor(Color.RED);
                    timer = System.currentTimeMillis();
                    plannedSteps = Integer.parseInt(stepsTodayVal.getText().toString());
                    setPlannedExerciseStatsVisibility(true);
                }
                else {
                    startStopBtn.setText("  Start Walk/Run  ");
                    startStopBtn.setBackgroundColor(Color.GREEN);
                    setPlannedExerciseStatsVisibility(false);
                    timer = System.currentTimeMillis() - timer;
                    plannedSteps = Integer.parseInt(stepsTodayVal.getText().toString()) - plannedSteps;

                    if(test_mode == ExecMode.EMode.DEFAULT) {
                        sd.getIntentionalStepsByDayStr(theTimer.getTodayString(), sp -> {
                            totalIntentionalSteps = sp + plannedSteps;
                            sd.setIntentionalStepsByDayStr(theTimer.getTodayString(), totalIntentionalSteps, null, null);
                            launchSummary(timer, plannedSteps);
                        });
                    }
                    else {
                        totalIntentionalSteps = sd.getIntentionalStepsByDayStr(theTimer.getTodayString(), null) + plannedSteps;
                        sd.setIntentionalStepsByDayStr(theTimer.getTodayString(), totalIntentionalSteps, null, null);
                        launchSummary(timer, plannedSteps);
                    }
                }
            }
        });

        // Check if this is the first time launching app
        if(sd.isFirstTimeUser()) {
            startActivity(new Intent(this, SetUpActivity.class));
            sd.setFirstTimeUser(false);
            // cannot remove otherwise tests fail
            if(test_mode == ExecMode.EMode.TEST_LOCAL) { sd.setCurrentGoal(GOAL_INIT, null, null); }
        }

        goalNum = sd.getCurrentGoal(null); // use goal num to initialize goal first TODO


        // setup user id
        if(test_mode == ExecMode.EMode.DEFAULT) {
            this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            fFireBaseAdapter = new FriendFireBaseAdapter(this.userId);
        } else {
            this.userId = TEST_CUR_USR_ID;
            fFireBaseAdapter = new MockFirebaseAdapter();
        }

        if(test_mode == ExecMode.EMode.DEFAULT) {
            sd.getCurrentGoal(gl -> {
                goalNum = gl;
                sd.setGoalByDayStr(theTimer.getTodayString(), goalNum, null, null);
                goalVal.setText(String.valueOf(goalNum));
                progressBar.setMax(goalNum);
                stepsLeftVal.setText(String.valueOf(goalNum - STEP_INIT));
            });
        }
        else {
            goalNum = sd.getCurrentGoal(null);
            sd.setGoalByDayStr(theTimer.getTodayString(), goalNum, null, null);
            goalVal.setText(String.valueOf(goalNum));
            progressBar.setMax(goalNum);
            stepsLeftVal.setText(String.valueOf(goalNum - STEP_INIT));
        }

        // Set Up Google Fitness
        fitnessServiceKey = FITNESS_SERVICE_KEY;
        StepCounterFactory.put(fitnessServiceKey, new StepCounterFactory.BluePrint() {
            @Override
            public StepCounter create(MainActivity activity) {
                return new StepCounterGoogleFit(activity);
            }
        });

        fitnessServiceKey = getIntent().getStringExtra(FITNESS_SERVICE_KEY);
        if (fitnessServiceKey == null ){
            fitnessServiceKey = FITNESS_SERVICE_KEY;
        }
        stepCounter = (StepCounterGoogleFit) StepCounterFactory.create( fitnessServiceKey, this);
        stepCounter.setup();
        stepCounter.addObserver(this);
        stepCounter.beginUpdates();

        // Update Button
        Button btnUpdateSteps = findViewById(R.id.btnUpdateSteps);
        btnUpdateSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepCounter.updateStepCount();
            }
        });

        setToday(theTimer.getTodayString());
        todayInt = theDate.getDay();

        hasFriend = true;
        fFireBaseAdapter.hasFriend(b -> {
            hasFriend = b;
            // yesterday
            // only once since data of yesterday never changes today
            if(!sd.isCheckedYesterdayGoal(today)) {
               sd.setCheckedYesterdayGoal(today);
                checkYesterdayGoalReach();
            }

            if(b) return;

            // not show sub-goal if goal met yesterday
            if(!sd.isShownYesterdayGoal(today) && !sd.isCheckedYesterdaySubGoal(today)) {
                sd.setCheckedYesterdaySubGoal(today);
                checkYesterdaySubGoalReach();
            }

            if(theTimer.isLateToday()) {
                checkSubGoalReach();
            }
        });

        this.addFriend = findViewById(R.id.AddFriend);
        this.addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAddFriendActivity();
            }
        });

        this.viewFriends = findViewById(R.id.viewFriends);
        this.viewFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchViewFriends();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(theTimer.isLateToday()) {
            checkSubGoalReach();
        }
        // update date
        if(todayInt != theDate.getDay()) {
            todayInt = theDate.getDay();
            setToday(theTimer.getTodayString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//       If authentication was required during google fit setup, this will be called after the user authenticates
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == stepCounter.getRequestCode()) {
                stepCounter.updateStepCount();
            }
        } else {
            Log.e(TAG, "ERROR, google fit result code: " + resultCode);
        }

        // update goals
        if(test_mode == ExecMode.EMode.DEFAULT) {
            sd.getCurrentGoal(gl -> {
                goalNum = gl;
                setGoal(goalNum);
            });
        }
        else {
            goalNum = sd.getCurrentGoal(null);
            setGoal(goalNum);
        }
    }

    // goal is reached but should we display the message?
    public void goalReach() {
        if(!sd.isShownGoal(today)) {
            sd.setShownGoal(today);
            Log.i(TAG, "Show goal on: " + today);
            goalReached(false);
        }
    }

    // has prompted sub goal?
    // has prompted goal???
    // has made progress?
    public void checkSubGoalReach() {
        if(hasFriend) return;
        int todaySteps = Integer.parseInt(stepsTodayVal.getText().toString());

        if(test_mode == ExecMode.EMode.DEFAULT) {
            sd.getStepsByDayStr(theTimer.getYesterdayString(), yesterdaySteps -> {
                if (!sd.isShownSubGoal(today) &&
                        !sd.isShownGoal(today) &&
                        todaySteps < goalNum &&
                        progressEncouragement.progressMade(todaySteps, yesterdaySteps)) {
                    Log.i(TAG, "Show sub goal on: " + today);
                    sd.setShownSubGoal(today);
                    progressEncouragement.showEncouragementMessage(todaySteps, yesterdaySteps);
                }
            });
        }
        else {
            int yesterdaySteps = sd.getStepsByDayStr(theTimer.getYesterdayString(), null);
            if (!sd.isShownSubGoal(today) &&
                    !sd.isShownGoal(today) &&
                    todaySteps < goalNum &&
                    progressEncouragement.progressMade(todaySteps, yesterdaySteps)) {
                Log.i(TAG, "Show sub goal on: " + today);
                sd.setShownSubGoal(today);
                progressEncouragement.showEncouragementMessage(todaySteps, yesterdaySteps);
            }
        }
    }

    // check if goal reached yesterday
    // need to be called only once per day
    protected void checkYesterdayGoalReach() {
        String yesterday = theTimer.getYesterdayString();

        if(test_mode == ExecMode.EMode.DEFAULT) {
            sd.getStepsByDayStr(theTimer.getYesterdayString(), yesterdaySteps -> {
                sd.getGoalByDayStr(theTimer.getYesterdayString(), yesterdayGoal -> {
                    if (!sd.isShownYesterdayGoal(today) &&
                            !sd.isShownGoal(yesterday) &&
                            yesterdayGoal <= yesterdaySteps) {
                        Log.i(TAG, "Show yesterday goal on: " + today);
                        sd.setShownYesterdayGoal(today);
                        goalReached(true);
                    }
                });
            });
        }
        else {
            int yesterdaySteps = sd.getStepsByDayStr(theTimer.getYesterdayString(), null);
            int yesterdayGoal = sd.getGoalByDayStr(theTimer.getYesterdayString(), null);


            if (!sd.isShownYesterdayGoal(today) &&
                    !sd.isShownGoal(yesterday) &&
                    yesterdayGoal <= yesterdaySteps) {
                Log.i(TAG, "Show yesterday goal on: " + today);
                sd.setShownYesterdayGoal(today);
                goalReached(true);
            }
        }
    }

    // check if sub goal reached yesterday
    // need to be called only once per day
    protected void checkYesterdaySubGoalReach() {
        if(hasFriend) return;
        String yesterday = theTimer.getYesterdayString();

        if (test_mode == ExecMode.EMode.DEFAULT) {
            sd.getStepsByDayStr(theTimer.getYesterdayString(), yesterdaySteps -> {
                sd.getGoalByDayStr(theTimer.getYesterdayString(), yesterdayGoal -> {
                    sd.getStepsByDayStr(ITimer.getDayStrDayBefore(theTimer.getTodayString(), 2), dayBeforeYesterdaySteps -> {
                        if (!sd.isShownYesterdaySubGoal(today) &&
                                !sd.isShownSubGoal(yesterday) &&
                                !sd.isShownYesterdayGoal(today) &&
                                yesterdaySteps < yesterdayGoal &&
                                progressEncouragement.progressMade(yesterdaySteps, dayBeforeYesterdaySteps)) {
                            Log.i(TAG, "Show yesterday sub goal on: " + today);
                            sd.setShownYesterdaySubGoal(today);
                            progressEncouragement.showEncouragementMessage(yesterdaySteps, dayBeforeYesterdaySteps);
                        }
                    });
                });
            });
        } else {
            int yesterdaySteps = sd.getStepsByDayStr(theTimer.getYesterdayString(), null);
            int yesterdayGoal = sd.getGoalByDayStr(theTimer.getYesterdayString(), null);
            int dayBeforeYesterdaySteps = sd.getStepsByDayStr(ITimer.getDayStrDayBefore(theTimer.getTodayString(), 2), null);

            if (!sd.isShownYesterdaySubGoal(today) &&
                    !sd.isShownSubGoal(yesterday) &&
                    !sd.isShownYesterdayGoal(today) &&
                    yesterdaySteps < yesterdayGoal &&
                    progressEncouragement.progressMade(yesterdaySteps, dayBeforeYesterdaySteps)) {
                Log.i(TAG, "Show yesterday sub goal on: " + today);
                sd.setShownYesterdaySubGoal(today);
                progressEncouragement.showEncouragementMessage(yesterdaySteps, dayBeforeYesterdaySteps);
            }
        }
    }

    protected void goalReached(boolean isYesterday) {
        Log.d(TAG,"Goal reached");
        Log.d(TAG,"Reached goal is yesterday's? " + isYesterday);
        String title = "You Reached the Goal";
        if(isYesterday) title += " Yesterday";
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle(title)
                .setMessage("Congratulations! Do you want to set a new step goal?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       launchSetGoalActivity();
                    }
                })
                .setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    public void setStepCount(int stepCount) {
        stepsTodayVal.setText(String.valueOf(stepCount));
        int stepsToGoal = (stepCount <= goalNum) ? goalNum - stepCount: 0;
        stepsLeftVal.setText(String.valueOf(stepsToGoal));
        progressBar.setProgress(stepCount);
        if(stepsToGoal == 0) goalReach();
    }


    public void setGoal(int goalNum) {
        this.goalNum = goalNum;
        this.goalVal.setText(String.valueOf(goalNum));
        sd.setGoalByDayStr(theTimer.getTodayString(), goalNum, null, null);
//        sd.setCurrentGoal(goalNum,null,null);

        // changing goal will also change progress
        int stepCount = Integer.valueOf(this.stepsTodayVal.getText().toString());
        int stepsToGoal = (stepCount <= goalNum) ? goalNum - stepCount: 0;
        stepsLeftVal.setText(String.valueOf(stepsToGoal));
        progressBar.setMax(goalNum);
        progressBar.setProgress(stepCount);
    }

    public int getGoal() {
        return this.goalNum;
    }

    // MM/dd/yyyy
    public void setToday(String today) {
        if(ITimer.isValidDayStr(today)) {
            this.today = today;
        } else {
            throw new IllegalArgumentException("Wrong date format");
        }
        // not updating todayInt here to make mocking work
    }


    // for test
    public void setTimer(ITimer t) {
        this.theTimer = t;
        setToday(theTimer.getTodayString());
    }

    public void setSavedDataManager(SavedDataManager sd) {
        this.sd = sd;
    }

    public void setTheDate(IDate d) {
        this.theDate = d;
        todayInt = theDate.getDay();
    }

    public void setPlannedExerciseStatsVisibility(boolean visible) {
        if(visible) {
            columns.setVisibility(View.VISIBLE);
            plannedTimeValue.setVisibility(View.VISIBLE);
            plannedStepValue.setVisibility(View.VISIBLE);
            plannedMPHValue.setVisibility(View.VISIBLE);
        }
        else {
            columns.setVisibility(View.INVISIBLE);
            plannedTimeValue.setVisibility(View.INVISIBLE);
            plannedStepValue.setVisibility(View.INVISIBLE);
            plannedMPHValue.setVisibility(View.INVISIBLE);
        }
    }

    public StepCounter getStepCounter(){
        return this.stepCounter;
    }

    // for test
    public void setFitnessServiceKey(String fitnessServiceKey) {
        this.fitnessServiceKey = fitnessServiceKey;
    }

    public void launchAddFriendActivity(){
        FirebaseUser curFirebaseUsr = FirebaseAuth.getInstance().getCurrentUser();
        String curFireBaseUid = curFirebaseUsr.getUid();
        this.userId = curFireBaseUid;
        // Initialize friendshipManager
        Intent intent = new Intent(this, BefriendActivity.class);
        intent.putExtra("id", this.userId);
        /*getIntent().getSerializableExtra("MyClass");
        Bundle bundle = new Bundle();
        bundle.putSerializable("FriendshipManager", this.friendshipManager);
        intent.putExtras(bundle);*/
        startActivity(intent);
    }

    public AlertDialog launchProgressChart(View view) {
        Intent intent = new Intent(this, ProgressChart.class);

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle(R.string.title_select_summary)
                .setMessage(R.string.msg_select_summary)
                .setPositiveButton(R.string.weekly_chart, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        intent.putExtra("mode", "week");
                        startActivity(intent);
                    }
                })
                .setNeutralButton(R.string.monthly_chart, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        intent.putExtra("mode", "month");
                        startActivity(intent);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    public void launchSummary(long timeElapsed, int stepsTaken) {
        Intent intent = new Intent(this, PlannedExerciseSummary.class);
        intent.putExtra("timeElapsed", timeElapsed);
        intent.putExtra("stepsTaken", stepsTaken);
        startActivity(intent);
    }

    public void launchSetGoalActivity(View view) {
        Intent intent = new Intent(this, SetGoalActivity.class);
        startActivityForResult(intent,0);
    }

    public void launchSetGoalActivity() {
        Intent intent = new Intent(this, SetGoalActivity.class);
        startActivityForResult(intent,0);
    }

    public void launchViewFriends() {
        Intent intent = new Intent(this, FriendListActivity.class);
        intent.putExtra("id", this.userId);
        startActivity(intent);
    }


}
