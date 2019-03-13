package com.android.personbest;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;
import com.android.personbest.SavedDataManager.SavedDataManager;
import com.android.personbest.SavedDataManager.SavedDataManagerFirestore;
import com.android.personbest.SavedDataManager.SavedDataManagerSharedPreference;
import com.android.personbest.StepCounter.IStatistics;
import com.android.personbest.Timer.ITimer;
import com.android.personbest.Timer.TimerSystem;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProgressChart extends AppCompatActivity {
    private static ExecMode.EMode test_mode;
    private ProgressUtils.IntervalMode mode;
    private SavedDataManager savedDataManager;
    private ArrayList<String> xAxisLabel;
    private List<Pair<String, Integer>> entries;
    private ITimer timer;
    private final String[] DAYOFWEEK = {
            DayOfWeek.of(7).getDisplayName(TextStyle.SHORT, Locale.US),
            DayOfWeek.of(1).getDisplayName(TextStyle.SHORT, Locale.US),
            DayOfWeek.of(2).getDisplayName(TextStyle.SHORT, Locale.US),
            DayOfWeek.of(3).getDisplayName(TextStyle.SHORT, Locale.US),
            DayOfWeek.of(4).getDisplayName(TextStyle.SHORT, Locale.US),
            DayOfWeek.of(5).getDisplayName(TextStyle.SHORT, Locale.US),
            DayOfWeek.of(6).getDisplayName(TextStyle.SHORT, Locale.US)
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_chart);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mode = getIntent().getStringExtra("mode").equals("week") ? ProgressUtils.IntervalMode.WEEK :
                ProgressUtils.IntervalMode.MONTH;

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

        // Create legend entries, this never changes after creation
        entries = ProgressUtils.createLegendEntries();

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

    public void setManager(SavedDataManager manager) {
        savedDataManager = manager;
    }

    public void quaryData() {
        if(test_mode == ExecMode.EMode.DEFAULT) {
            if(mode == ProgressUtils.IntervalMode.WEEK) {
                savedDataManager.getLastWeekSteps(updateTime(), this::buildChart);
            }
            else {
                savedDataManager.getLastMonthStat(updateTime(), this::buildChart);
            }
        }
        else {
            if(mode == ProgressUtils.IntervalMode.WEEK) {
                buildChart(savedDataManager.getLastWeekSteps(updateTime(), null));
            }
            else {
                buildChart(savedDataManager.getLastMonthStat(updateTime(), null));
            }
        }

    }

    public void buildChart(List<IStatistics> stats) {
        ChartBuilder builder = new ChartBuilder(this);
        builder.setData(stats, 28)
                .setXAxisLabel(xAxisLabel)
                .setLegend(entries)
                .useOptimalConfig()
                .show();
    }

    private void createAxisLabels(String today, ProgressUtils.IntervalMode mode) {
        // Create Axis
        xAxisLabel = new ArrayList<>();
        xAxisLabel.add("");
        for(int i = 0; i < 28; ++i) {
            StringBuilder dateSB = new StringBuilder(String.valueOf(timer.getDayStampDayBefore(today, 28 - i)));
            dateSB.insert(6, '/');
            xAxisLabel.add(dateSB.substring(4));
        }
    }

    private String updateTime() {
        String today = timer.getTodayString();
        createAxisLabels(today);
        return today;
    }
}