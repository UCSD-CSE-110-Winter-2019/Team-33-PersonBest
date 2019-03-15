package com.android.personbest;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
            savedDataManager = new SavedDataManagerSharedPreference(this); // TODO a mock firestore adapter
        } else if (test_mode == ExecMode.EMode.TEST_LOCAL) {
            savedDataManager = new SavedDataManagerSharedPreference(this);
        }
        else {
            // set saved data manager
            savedDataManager = new SavedDataManagerFirestore(this);
        }

        // New Timer
        timer = new TimerSystem();

        final Activity self = this;

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                ((ProgressChart) self).quaryData();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.quaryData();
    }

    public void quaryData() {
        if(test_mode == ExecMode.EMode.DEFAULT) {
            savedDataManager.getLastMonthStat(timer.getTodayString(), this::buildChart);
        }
        else {
            buildChart(savedDataManager.getLastMonthStat(timer.getTodayString(), null));
        }

    }

    public void buildChart(List<IStatistics> stats) {
        ChartBuilder builder = new ChartBuilder(this);
        builder.setData(stats)
                .setInterval(mode, timer.getTodayString())
                .buildChartData()
                .buildTimeAxisLabel()
                .buildWalkEntryLegends()
                .useOptimalConfig()
                .show();
    }

    public void setManager(SavedDataManager manager) {
        savedDataManager = manager;
    }

    public void setTimer(ITimer tm) {
        timer = tm;
    }
}