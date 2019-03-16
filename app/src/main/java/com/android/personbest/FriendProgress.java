package com.android.personbest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.widget.Button;
import com.android.personbest.Chart.*;
import com.android.personbest.SavedDataManager.SavedDataManager;
import com.android.personbest.SavedDataManager.SavedDataManagerFirestore;
import com.android.personbest.SavedDataManager.SavedDataManagerSharedPreference;
import com.android.personbest.StepCounter.IStatistics;
import com.android.personbest.Timer.ITimer;
import com.android.personbest.Timer.TimerSystem;

import java.util.ArrayList;
import java.util.List;

public class FriendProgress extends AppCompatActivity {
    private static final String TAG = "Monthly Chart";
    private static final int NUM_DAYS_M = 28;
    private static ExecMode.EMode test_mode;

    private SavedDataManager savedDataManager;
    private ArrayList<String> xAxisLabel;
    private List<Pair<String, Integer>> entries;
    private String user;
    private String chatId;
    private ITimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_progress);

        // we testing?
        test_mode = ExecMode.getExecMode();
        if(test_mode == ExecMode.EMode.TEST_CLOUD) {
            SavedDataManager f = (SavedDataManager) getIntent().getSerializableExtra("SavedDataManager");
            if (f == null) {
                savedDataManager = new SavedDataManagerSharedPreference(this);
            } else {
                savedDataManager = f;
            }
        } else if (test_mode == ExecMode.EMode.TEST_LOCAL) {
            savedDataManager = new SavedDataManagerSharedPreference(this);
        }
        else {
            // set saved data manager
            savedDataManager = new SavedDataManagerFirestore(this);
        }

        // New Timer
        timer = new TimerSystem();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Query and create chart
        user = getIntent().getStringExtra("userId");
        chatId = getIntent().getStringExtra("chatId");

        Button send = findViewById(R.id.msg_button);
        send.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChatBoxActivity.class);
            intent.putExtra("chatId", chatId);
            startActivity(intent);
        });

        quaryData(user);
    }

    private void quaryData(String user) {
        Log.i(TAG,"Quary data from firebase, friend=" + user);
        if(test_mode == ExecMode.EMode.DEFAULT) {
            savedDataManager.getFriendMonthlyStat(user, timer.getTodayString(), this::buildChart);
        }
    }

    private void buildChart(List<IStatistics> stats) {
        Log.i(TAG,"Build chart");
        ChartBuilder builder = new ChartBuilder(this);
        builder.setData(stats)
                .setInterval(IntervalMode.MONTH, timer.getTodayString())
                .buildChartData()
                .buildTimeAxisLabel()
                .buildWalkEntryLegends()
                .useOptimalConfig()
                .show();
    }
}
