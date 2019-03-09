package com.android.personbest;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import com.android.personbest.SavedDataManager.SavedDataManager;
import com.android.personbest.SavedDataManager.SavedDataManagerFirestore;
import com.android.personbest.SavedDataManager.SavedDataManagerSharedPreference;
import com.android.personbest.StepCounter.IStatistics;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.*;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.StackedValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProgressChart extends AppCompatActivity {
    private static ExecMode.EMode test_mode;
    private List<IStatistics> stepStats;
    private List<BarEntry> barEntries;
    private List<Entry> lineEntries;
    private SavedDataManager savedDataManager;
    private int date;
    private CombinedChart progressChart;
    private CombinedData data;
    private ArrayList<String> xAxisLabel;
    private String stats;
    private SharedPreferences sp;
    private String todayStr = "03/02/2019"; // Default day
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

        barEntries = new ArrayList<>();
        lineEntries = new ArrayList<>();

        String tmpTodayStr = getIntent().getStringExtra("todayStr");
        if(tmpTodayStr != null)
            todayStr = tmpTodayStr;

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

        final Activity self = this;

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                ((ProgressChart) self).setDate(todayStr);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressChart = findViewById(R.id.progressChart);

        this.setDate(todayStr);
    }

    public void setManager(SavedDataManager manager) {
        savedDataManager = manager;
    }

    public void setDate(String today) {
        if(test_mode == ExecMode.EMode.DEFAULT) {
            savedDataManager.getLastWeekSteps(today, ss -> {
                stepStats = ss;
                setBarChart(stepStats);
                showChart();
            });
        }
        else {
            stepStats = savedDataManager.getLastWeekSteps(today, null);
            setBarChart(stepStats);
            showChart();
        }

    }


    // Some of the configuration ideas (like axis) used this tutorial
    // https://www.studytutorial.in/android-combined-line-and-bar-chart-using-mpandroid-library-android-tutorial
    // as reference
    public void setBarChart(List<IStatistics> stepStats) {

        createEntries(stepStats);

        // Create Axis
        xAxisLabel = new ArrayList<>();
        xAxisLabel.add("");
        int j = 6;
        for(BarEntry be: barEntries) {
            j = j % 7 + 1;
            xAxisLabel.add(DayOfWeek.of(j).getDisplayName(TextStyle.SHORT, Locale.US));
        }

        // Add daily stats
        stats = createStatsStr(stepStats);

        // Set data
        data = new CombinedData();
        data.setData(createBarData());
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

    private void showChart() {
        progressChart.setData(data);
        progressChart.invalidate();
        ((TextView)findViewById(R.id.statsView)).setText(stats);
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

    private BarData createBarData() {
        BarDataSet stepDataSet = new BarDataSet(barEntries, "Steps Current Week");
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

    public String createStatsStr(List<IStatistics> stepStats) {
        StringBuilder sb = new StringBuilder();
        sb.append("Intentional Work Statistics\n");
        for(int i = 0; i < stepStats.size(); ++i) {
            sb.append(DAYOFWEEK[i] + ": ");
            sb.append(stepStats.get(i).getStats() + '\n');
        }
        return sb.toString();
    }

    public LineData getLineData() {
        return data.getLineData();
    }

    public BarData getBarData() {
        return data.getBarData();
    }

    public ArrayList<String> getXAxisLabel() {
        return xAxisLabel;
    }

    // Not used for now, but will keep this in case we need it in the future
    public static class EnhancedStackedValueFormatter extends StackedValueFormatter {
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