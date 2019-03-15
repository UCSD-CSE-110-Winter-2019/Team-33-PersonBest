package com.android.personbest;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import com.android.personbest.Chart.ChartBuilder;
import com.android.personbest.Chart.IntervalMode;
import com.android.personbest.SavedDataManager.SavedDataManager;
import com.android.personbest.SavedDataManager.SavedDataManagerFirestore;
import com.android.personbest.SavedDataManager.SavedDataManagerSharedPreference;
import com.android.personbest.StepCounter.IStatistics;
import com.android.personbest.Timer.ITimer;
import com.android.personbest.Timer.TimerSystem;

import java.util.List;

public class ProgressChart extends AppCompatActivity {
    private static String TAG = "ProgressChart";
    private static ExecMode.EMode test_mode;
    private IntervalMode mode;
    private SavedDataManager savedDataManager;
    private ITimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_chart);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mode = getIntent().getStringExtra("mode").equals("week") ? IntervalMode.WEEK :
                IntervalMode.MONTH;

        // we testing?
        test_mode = ExecMode.getExecMode();
        if(test_mode == ExecMode.EMode.TEST_CLOUD) {
            savedDataManager = new SavedDataManagerSharedPreference(this);
        } else if (test_mode == ExecMode.EMode.TEST_LOCAL) {
            savedDataManager = new SavedDataManagerSharedPreference(this);
        }
        else {
            // set saved data manager
            savedDataManager = new SavedDataManagerFirestore(this);
        }

        // New Timer
        timer = new TimerSystem();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            this.quaryData();
            Log.i(TAG, "Refreshed");
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.quaryData();
    }

    private void quaryData() {
        Log.i(TAG,"Quary data from firebase");
        if(test_mode == ExecMode.EMode.DEFAULT) {
            savedDataManager.getLastMonthStat(timer.getTodayString(), this::buildChart);
        }
    }

    private void buildChart(List<IStatistics> stats) {
        Log.i(TAG,"Build chart");
        ChartBuilder builder = new ChartBuilder(this);
        builder.setData(stats)
                .setInterval(mode, timer.getTodayString())
                .buildChartData()
                .buildTimeAxisLabel()
                .buildWalkEntryLegends()
                .useOptimalConfig()
                .show();
    }

    public void setTimer(ITimer newTimer) {
        timer = newTimer;
    }
}