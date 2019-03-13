package com.android.personbest;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import com.android.personbest.SavedDataManager.SavedDataManager;
import com.android.personbest.SavedDataManager.SavedDataManagerFirestore;
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

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MonthlyChart extends AppCompatActivity {
    private static final String TAG = "Monthly Chart";
    private static final int NUM_DAYS_M = 28;
    private static ExecMode.EMode test_mode;
    private List<IStatistics> stepStats;
    private List<BarEntry> barEntries;
    private List<Entry> lineEntries;
    private SavedDataManager savedDataManager;
    private CombinedChart progressChart;
    private CombinedData data;
    private ArrayList<String> xAxisLabel;
    private String stats;
    private String user;
    private ITimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_chart);

        Toolbar toolbar = findViewById(R.id.toolbar);

        EditText msg = findViewById(R.id.msg_box);


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

        barEntries = new ArrayList<>();
        lineEntries = new ArrayList<>();
        timer = new TimerSystem();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressChart = findViewById(R.id.progressChart);
        user = getIntent().getStringExtra("userId");
        queryData(timer.getTodayString(), user);
    }

    public void setManager(SavedDataManager manager) {
        savedDataManager = manager;
    }

    public void setTimer(ITimer tm) {
        timer = tm;
    }

    public void queryData(String today, String user) {
        if(test_mode == ExecMode.EMode.DEFAULT) {
            savedDataManager.getFriendMonthlyStat(user, today, ss -> {
                stepStats = ss;
                setBarChart();
                showChart();
            });
        }
        else {
            stepStats = savedDataManager.getLastWeekSteps(today, null);
            setBarChart();
            showChart();
        }
    }


    // Some of the configuration ideas (like axis) used this tutorial
    // https://www.studytutorial.in/android-combined-line-and-bar-chart-using-mpandroid-library-android-tutorial
    // as reference
    public void setBarChart() {
        if(stepStats.size() < NUM_DAYS_M) {
            padZero();
        }

        createEntries(stepStats);

        // Create Axis
        xAxisLabel = new ArrayList<>();
        xAxisLabel.add("");

        for(int i = 0; i < barEntries.size(); ++i) {
            String date = "" + ITimer.getDayStampDayBefore(timer.getTodayString(), NUM_DAYS_M - i);
            xAxisLabel.add(date.substring(4));
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
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0.6f);
        xAxis.setGranularity(1f);
        xAxis.setAxisMaximum(data.getXMax() + 0.4f);
        progressChart.getDescription().setEnabled(false);
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

    private void padZero() {
        ArrayList<IStatistics> zeros = new ArrayList<>();
        for(int i = 0; i < NUM_DAYS_M - stepStats.size(); ++i) {
            zeros.add(new DailyStat(0,0,0,1,0));
        }
        for(IStatistics iStat: stepStats) {
            zeros.add(iStat);
        }
        stepStats = zeros;
    }
}
