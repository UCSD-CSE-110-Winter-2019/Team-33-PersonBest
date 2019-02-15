package com.android.personbest;

import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.android.personbest.SavedDataManager.SavedDataManager;
import com.android.personbest.SavedDataManager.SavedDataManagerSharedPreference;
import com.android.personbest.StepCounter.DailyStat;
import com.android.personbest.StepCounter.IStatistics;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.security.InvalidParameterException;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EmptyProgressChart extends AppCompatActivity {

    private List<BarEntry> entries;
    private SavedDataManager savedDataManager;
    private int date;
    private BarChart progressChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_progress_chart);

        entries = new ArrayList<>();
        savedDataManager = new SavedDataManagerSharedPreference(this);

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
        progressChart.invalidate();
        entries.clear();
        List<IStatistics> stepStats = new ArrayList<>();//stepCounter.getLastWeekSteps(date);
        ArrayList<String> xAxis = new ArrayList<>();

        stepStats.add(new DailyStat(5000, 8226, 3255, null));
        stepStats.add(new DailyStat(5000, 2542, 3752, null));
        stepStats.add(new DailyStat(5000, 1939, 882, null));
        stepStats.add(new DailyStat(5000, 3755, 3078, null));
        stepStats.add(new DailyStat(5000, 2188, 3673, null));
        stepStats.add(new DailyStat(5000, 6731, 9225, null));
        stepStats.add(new DailyStat(5000, 7633, 6706, null));

        int i = 0;
        for (IStatistics stat : stepStats) {
            i += 1;
            entries.add(new BarEntry(i,stat.getIntentionalWalk()));
            xAxis.add(DayOfWeek.of(i).getDisplayName(TextStyle.SHORT, Locale.JAPAN));
        }
        BarDataSet stepDataSet = new BarDataSet(entries, "Steps Current Week");
        stepDataSet.setColor(Color.rgb(123, 144, 210));
        BarData stepData = new BarData(stepDataSet);
        progressChart.setData(stepData);
    }
}
