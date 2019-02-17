package com.android.personbest;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.android.personbest.SavedDataManager.SavedDataManager;
import com.android.personbest.SavedDataManager.SavedDataManagerSharedPreference;
import com.android.personbest.StepCounter.DailyStat;
import com.android.personbest.StepCounter.IStatistics;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.StackedValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

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

        Toolbar toolbar = findViewById(R.id.toolbar);
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
        if (date < 0 || date >= 7)
            throw new InvalidParameterException("Date expected between 0 and 6, but got" + date);
        this.date = date;
        setBarChart();
    }

    public void setBarChart() {
        List<IStatistics> stepStats = new ArrayList<>();//stepCounter.getLastWeekSteps(date);


        stepStats.add(new DailyStat(5000, 8226, 3255, null));
        stepStats.add(new DailyStat(5000, 6260, 3752, null));
        stepStats.add(new DailyStat(5000, 1939, 882, null));
        stepStats.add(new DailyStat(5000, 3755, 3078, null));
        stepStats.add(new DailyStat(5000, 4530, 3673, null));
        stepStats.add(new DailyStat(5000, 9934, 9225, null));
        stepStats.add(new DailyStat(5000, 7633, 6706, null));


        ArrayList<String> xAxis = new ArrayList<>();
        ArrayList<LegendEntry> legendEntries = new ArrayList<>();
        xAxis.add("");
        createBarEntries(stepStats);
        int j = 6;
        for(BarEntry be: entries) {
            j = j % 7 + 1;
            xAxis.add(DayOfWeek.of(j).getDisplayName(TextStyle.SHORT, Locale.US)/* + "\nIncidental Walk: " +
                                                     be.getYVals()[0] + "\nIntentional Walk: " + be.getYVals()[1]*/);
        }

        BarDataSet stepDataSet = new BarDataSet(entries, "Steps Current Week");
        //stepDataSet.setValueFormatter(new EnhancedStackedValueFormatter(null));
        stepDataSet.setColors(Color.rgb(0, 92, 175), Color.rgb(123, 144, 210));
        BarData stepData = new BarData(stepDataSet);
        legendEntries.add(new LegendEntry("Incidental Walk", Legend.LegendForm.DEFAULT,
                Float.NaN, Float.NaN, null, Color.rgb(0, 92, 175)));
        legendEntries.add(new LegendEntry("Intentional Walk", Legend.LegendForm.DEFAULT,
                Float.NaN, Float.NaN, null, Color.rgb(123, 144, 210)));

        progressChart.setData(stepData);
        progressChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxis));
        progressChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        LimitLine l = new LimitLine(5000);
        l.setLabel("Goal");
        l.setLineColor(Color.rgb(0, 0, 0));
        progressChart.getAxisLeft().addLimitLine(l);
        progressChart.getLegend().setCustom(legendEntries);
        progressChart.setTouchEnabled(false);
        progressChart.invalidate();
    }

    private void createBarEntries(List<IStatistics> stepStats) {
        entries.clear();
        int i = 0;
        for (IStatistics stat : stepStats) {
            i += 1;
            float[] tempArr = new float[2];
            tempArr[0] = stat.getIncidentWalk();
            tempArr[1] = stat.getIntentionalWalk();
            entries.add(new BarEntry(i, tempArr));
        }
    }

    class EnhancedStackedValueFormatter extends StackedValueFormatter {
        private String[] appendices;
        public EnhancedStackedValueFormatter(String[] appendices) {
            super(false, null, 0);
            this.appendices = appendices;
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return "null";
        }
    }
}