package com.android.personbest;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.android.personbest.SavedDataManager.SavedDataManager;
import com.android.personbest.SavedDataManager.SavedDataManagerSharedPreference;
import com.android.personbest.StepCounter.DailyStat;
import com.android.personbest.StepCounter.IStatistics;
import com.android.personbest.StepCounter.StepCounter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.security.InvalidParameterException;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProgressChart extends AppCompatActivity {
    private List<BarEntry> entries;
    private SavedDataManager savedDataManager;
    private int date;
    private BarChart progressChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_chart);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        entries = new ArrayList<>();
        savedDataManager = new SavedDataManagerSharedPreference(this);

        final Activity self = this;

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                ((ProgressChart) self).setDate(ZonedDateTime.now(ZoneId.systemDefault()).getDayOfWeek().getValue() - 1);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressChart = findViewById(R.id.progressChart);

        setDate(ZonedDateTime.now(ZoneId.systemDefault()).getDayOfWeek().getValue() - 1);
    }

    public void setDate(int date) {
        if(date < 0 || date >= 7)
            throw new InvalidParameterException("Date expected between 0 and 6, but got" + date);
        this.date = date;
        setBarChart();
    }

    public void setBarChart() {
        entries.clear();
        List<IStatistics> stepStats = new ArrayList<>();//stepCounter.getLastWeekSteps(date);
        ArrayList<String> xAxis = new ArrayList<>();

        stepStats.add(new DailyStat(5000, 8226, 3255, null));
        stepStats.add(new DailyStat(5000, 6260, 3752, null));
        stepStats.add(new DailyStat(5000, 1939, 882, null));
        stepStats.add(new DailyStat(5000, 3755, 3078, null));
        stepStats.add(new DailyStat(5000, 4530, 3673, null));
        stepStats.add(new DailyStat(5000, 9934, 9225, null));
        stepStats.add(new DailyStat(5000, 7633, 6706, null));

        int i = 0;
        for (IStatistics stat : stepStats) {
            i += 1;
            float[] tempArr = new float[2];
            tempArr[0] = stat.getIncidentWalk();
            tempArr[1] = stat.getIntentionalWalk();
            entries.add(new BarEntry(i, tempArr));
            xAxis.add(DayOfWeek.of(i).getDisplayName(TextStyle.FULL, Locale.JAPAN));
        }
        BarDataSet stepDataSet = new BarDataSet(entries, "Steps Current Week");
        stepDataSet.setColors(Color.rgb(0, 92, 175), Color.rgb(123, 144, 210));
        BarData stepData = new BarData(stepDataSet);
        progressChart.setData(stepData);
        progressChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxis));
        progressChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        progressChart.invalidate();
    }
}
