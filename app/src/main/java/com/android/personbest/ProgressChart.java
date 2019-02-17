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
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.*;
import com.github.mikephil.charting.data.*;
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
    private List<BarEntry> barEntries;
    private List<Entry> lineEntries;
    private SavedDataManager savedDataManager;
    private int date;
    private CombinedChart progressChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_chart);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        barEntries = new ArrayList<>();
        lineEntries = new ArrayList<>();
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


    // Some of the configuration ideas (like axis) used this tutorial
    // https://www.studytutorial.in/android-combined-line-and-bar-chart-using-mpandroid-library-android-tutorial
    // as reference
    public void setBarChart() {
        List<IStatistics> stepStats = new ArrayList<>();//stepCounter.getLastWeekSteps(date);

        // FIXME
        stepStats.add(new DailyStat(5000, 8226, 3255, String.format("S:3255\nT:1:30:02\nD:%.2fmi", 3255/2435f)));
        stepStats.add(new DailyStat(5540, 6260, 3752, String.format("S:3752\nT:1:43:31\nD:%.2fmi", 3752/2435f)));
        stepStats.add(new DailyStat(6000, 1939, 882, String.format("S:1939\nT:0:50:45\nD:%.2fmi", 882/2435f)));
        stepStats.add(new DailyStat(6500, 3755, 3078, String.format("S:3078\nT:1:23:37\nD:%.2fmi", 3078/2435f)));
        stepStats.add(new DailyStat(6875, 4530, 3673, String.format("S:3673\nT:1:39:40\nD:%.2fmi", 3673/2435f)));
        stepStats.add(new DailyStat(7500, 9934, 9225, String.format("S:9225\nT:4:08:29\nD:%.2fmi", 9225/2435f)));
        stepStats.add(new DailyStat(8500, 7633, 6706, String.format("S:6706\nT:3:01:57\nD:%.2fmi", 6706/2435f)));

        createEntries(stepStats);

        ArrayList<String> xAxisLabel = new ArrayList<>();
        xAxisLabel.add("");
        int j = 6;
        for(BarEntry be: barEntries) {
            j = j % 7 + 1;
            xAxisLabel.add(DayOfWeek.of(j).getDisplayName(TextStyle.SHORT, Locale.US));
        }

        ArrayList<String> stats = new ArrayList<>();
        for(IStatistics stat : stepStats) {
            stats.add(stat.getStats());
        }

        // Set data
        CombinedData data = new CombinedData();
        data.setData(createBarData(stats.toArray(new String[stats.size()])));
        data.setData(createLineData());

        // Draw options
        progressChart.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.BAR,
                                                                  CombinedChart.DrawOrder.LINE});
        progressChart.setDrawGridBackground(false);

        // Legend options
        ArrayList<LegendEntry> legendEntries = new ArrayList<>();
        legendEntries.add(new LegendEntry("Incidental Walk", Legend.LegendForm.DEFAULT,
                Float.NaN, Float.NaN, null, Color.rgb(0, 92, 175)));
        legendEntries.add(new LegendEntry("Intentional Walk", Legend.LegendForm.DEFAULT,
                Float.NaN, Float.NaN, null, Color.rgb(123, 144, 210)));

        Legend legend = progressChart.getLegend();
        legend.setCustom(legendEntries);

        //Axis options
        YAxis rightAxis = progressChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f);

        YAxis leftAxis = progressChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f);

        XAxis xAxis = progressChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0.6f);
        xAxis.setGranularity(1f);
        xAxis.setAxisMaximum(data.getXMax() + 0.4f);

        progressChart.getDescription().setEnabled(false);

        progressChart.setData(data);
        progressChart.invalidate();
    }

    private void createEntries(List<IStatistics> stepStats) {
        barEntries.clear();
        lineEntries.clear();
        int i = 0;
        for (IStatistics stat : stepStats) {
            i += 1;
            float[] tempArr = new float[2];
            tempArr[0] = stat.getIncidentWalk();
            tempArr[1] = stat.getIntentionalWalk();
            barEntries.add(new BarEntry(i, tempArr));
            lineEntries.add(new Entry(i, stat.getGoal()));
        }
    }

    private BarData createBarData(String[] appendices) {
        BarDataSet stepDataSet = new BarDataSet(barEntries, "Steps Current Week");
        stepDataSet.setValueFormatter(new EnhancedStackedValueFormatter(appendices));
        stepDataSet.setColors(Color.rgb(0, 92, 175), Color.rgb(123, 144, 210));
        stepDataSet.setValueTextSize(8f);
        stepDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        BarData bd =  new BarData(stepDataSet);
        bd.setBarWidth(0.4f);
        return bd;
    }

    private LineData createLineData() {
        LineDataSet goalDataSet = new LineDataSet(lineEntries, "Goals Current Week");
        goalDataSet.setColors(Color.rgb(8, 25, 45));
        goalDataSet.setCircleColor(Color.rgb(190, 194, 63));
        goalDataSet.setCircleRadius(3f);
        goalDataSet.setFillColor(Color.rgb(218, 201, 166));
        goalDataSet.setValueTextSize(8f);
        goalDataSet.setValueTextColor(Color.rgb(54, 86, 60));
        goalDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

        goalDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        return new LineData(goalDataSet);
    }

    class EnhancedStackedValueFormatter extends StackedValueFormatter {
        private String[] appendices;
        public EnhancedStackedValueFormatter(String[] appendices) {
            super(false, null, 0);
            this.appendices = appendices;
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            float[] vals = ((BarEntry)entry).getYVals();

            if (vals != null) {
                // find out if we are on top of the stack
                if (vals[vals.length - 1] == value) {

                    return appendices[(int)entry.getX() - 1];
                } else {
                    return ""; // return empty
                }
            }
            return null;
        }
    }
}