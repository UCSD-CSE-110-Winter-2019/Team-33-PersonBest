package com.android.personbest;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import com.android.personbest.SavedDataManager.SavedDataManager;
import com.android.personbest.SavedDataManager.SavedDataManagerSharedPreference;
import com.android.personbest.StepCounter.DailyStat;
import com.android.personbest.StepCounter.IStatistics;
import com.android.personbest.Timer.ITimer;
import com.android.personbest.Timer.TimerSystem;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.StackedValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MonthlyChart extends AppCompatActivity {
    private List<BarEntry> barEntries;
    private List<Entry> lineEntries;
    private SavedDataManager savedDataManager;
    private CombinedChart progressChart;
    private CombinedData data;
    private ArrayList<String> xAxisLabel;
    private String stats;
    private String user = "jinghao@eugen.ucsd.edu";
    private ITimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_chart);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        EditText msg = findViewById(R.id.msg_box);


        barEntries = new ArrayList<>();
        lineEntries = new ArrayList<>();
        savedDataManager = new SavedDataManagerSharedPreference(this);
        timer = new TimerSystem();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressChart = findViewById(R.id.progressChart);

        queryData(timer.getTodayString(), user);
    }

    public void setManager(SavedDataManager manager) {
        savedDataManager = manager;
    }

    public void setTimer(ITimer tm) {
        timer = tm;
    }

    public void queryData(String date, String user) {
        Random rand = new Random();

        List<IStatistics> stepStats = new ArrayList<>();//savedDataManager.getLastWeekSteps(date);
        for(int i = 0; i < 28; ++i)
            stepStats.add(new DailyStat((int)(rand.nextDouble() * 5000) + 5000, (int)(rand.nextDouble() * 5000) + 5000, 0, 0, 0));
        setBarChart(stepStats);
        showChart();
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
        legendEntries.add(new LegendEntry("Total Steps", Legend.LegendForm.DEFAULT,
                Float.NaN, Float.NaN, null, Color.rgb(0, 92, 175)));

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
        //xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0.6f);
        xAxis.setGranularity(1f);
        xAxis.setAxisMaximum(data.getXMax() + 0.4f);

        //progressChart.getDescription().setEnabled(false);
    }

    private void showChart() {
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

    public LineData getLineData() {
        return data.getLineData();
    }

    public BarData getBarData() {
        return data.getBarData();
    }

    public ArrayList<String> getXAxisLabel() {
        return xAxisLabel;
    }
}
