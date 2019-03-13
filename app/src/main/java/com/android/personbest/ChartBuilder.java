package com.android.personbest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.Pair;
import com.android.personbest.StepCounter.DailyStat;
import com.android.personbest.StepCounter.IStatistics;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class ChartBuilder {
    private static final String TAG = "Chart Builder";
    private List<IStatistics> stepStats;
    private List<BarEntry> barEntries;
    private List<Entry> lineEntries;
    private List<LegendEntry> legendEntries;
    private CombinedChart progressChart;
    private CombinedData data;
    private int length;
    private Activity activity;

    public ChartBuilder(Activity act) {
        activity = act;
        progressChart = activity.findViewById(R.id.progressChart);
        barEntries = new ArrayList<>();
        lineEntries = new ArrayList<>();
    }

    public ChartBuilder setData(List<IStatistics> stats, int periodLength) {
        Log.i(TAG, "Set data, length=" + periodLength);

        stepStats = stats;
        length = periodLength;
        if(stepStats.size() < length) {
            padZero();
        }

        // Create data entries
        createEntries(stepStats);

        // Set data
        data = new CombinedData();
        data.setData(createBarData());
        data.setData(createLineData());

        return this;
    }

    public void show() {
        Log.i(TAG, "Show chart");
        progressChart.setData(data);
        progressChart.invalidate();
    }

    // Some of the configuration ideas (like axis) used this tutorial
    // https://www.studytutorial.in/android-combined-line-and-bar-chart-using-mpandroid-library-android-tutorial
    // as reference
    public ChartBuilder useOptimalConfig() {
        Log.i(TAG, "Use optimal configuration");

        // Draw options
        progressChart.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.BAR,
                CombinedChart.DrawOrder.LINE});
        progressChart.setDrawGridBackground(false);

        //Axis options
        YAxis rightAxis = progressChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f);

        YAxis leftAxis = progressChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f);

        XAxis xAxis = progressChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0.6f);
        xAxis.setGranularity(1f);
        xAxis.setAxisMaximum(data.getXMax() + 0.4f);
        progressChart.getDescription().setEnabled(false);

        return this;
    }

    public ChartBuilder setLegend(ArrayList<Pair<String, Integer>> entries) {
        Log.i(TAG, "Set custom legend entries");

        legendEntries = new ArrayList<>();

        for(int i = 0; i < entries.size(); ++i) {
            legendEntries.add(new LegendEntry(entries.get(i).first, Legend.LegendForm.DEFAULT,
                    Float.NaN, Float.NaN, null, entries.get(i).second));
        }
        progressChart.getLegend().setCustom(legendEntries);
        return this;
    }

    public ChartBuilder setXAxisLabel(ArrayList<String> xAxisLabel) {
        Log.i(TAG, "Set custom x axis");

        progressChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
        return this;
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

    private void padZero() {
        ArrayList<IStatistics> zeros = new ArrayList<>();
        for(int i = 0; i < length - stepStats.size(); ++i) {
            zeros.add(new DailyStat(0,0,0,1,0));
        }
        for(IStatistics iStat: stepStats) {
            zeros.add(iStat);
        }
        stepStats = zeros;
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

    public LineData getLineData() {
        return data.getLineData();
    }

    public BarData getBarData() {
        return data.getBarData();
    }
}
