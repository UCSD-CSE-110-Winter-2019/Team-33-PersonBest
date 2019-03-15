package com.android.personbest;


import android.app.Activity;
import android.content.Intent;

import com.android.personbest.Chart.ChartBuilder;
import com.android.personbest.Chart.IntervalMode;
import com.android.personbest.SavedDataManager.SavedDataManager;
import com.android.personbest.SavedDataManager.SavedDataManagerSharedPreference;
import com.android.personbest.StepCounter.*;

import com.android.personbest.Timer.ITimer;
import com.android.personbest.Timer.TimerMock;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.data.*;
import edu.emory.mathcs.backport.java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestChartBuilder {
    private static final String TODAY = "09-09-2019";
    private ChartBuilder cb;
    private List<IStatistics> stepStats;

    @Before
    public void setup() {
        ExecMode.setExecMode(ExecMode.EMode.TEST_LOCAL);
        cb = new ChartBuilder();
        stepStats = new ArrayList<>();
        stepStats.add(new DailyStat(5000, 8226, 3255, 1200000, 4.0f));
        stepStats.add(new DailyStat(5540, 6260, 3752, 1200000, 4.0f));
        stepStats.add(new DailyStat(6000, 1939, 882, 1200000, 4.0f));
        stepStats.add(new DailyStat(6500, 3755, 3078, 1200000, 4.0f));
        stepStats.add(new DailyStat(6875, 4530, 3673, 1200000, 4.0f));
        stepStats.add(new DailyStat(7500, 9934, 9225, 1200000, 4.0f));
        stepStats.add(new DailyStat(8500, 7633, 6706, 1200000, 4.0f));
        stepStats.add(new DailyStat(8620, 7712, 2342, 1200000, 4.0f));

    }

    @Test
    public void testsSetInterval1() {
        // Get resized stats from builder
        List<IStatistics> stats = cb.setData(stepStats).setInterval(IntervalMode.WEEK, TODAY).getStepStats();
        assertEquals(7, stats.size());
        for(int i = 0; i < stats.size(); ++i) {
            assertEquals(stepStats.get(i + 1), stats.get(i));
        }
    }

    @Test
    public void testSetInterval2() {
        // Get resized stats from builder
        List<IStatistics> stats = cb.setData(stepStats).setInterval(IntervalMode.MONTH, TODAY).getStepStats();
        assertEquals(28, stats.size());
        for(int i = 0; i < stats.size(); ++i) {
            if ((i - 20) < 0) {
                assertEquals(new DailyStat(0,0,0,1,0), stats.get(i));
            }
            else {
                assertEquals(stepStats.get(i - 20), stats.get(i));
            }
        }
    }

    @Test
    public void testBuildTimeAxisLabel() {
        // Get axis labels from builder
        List<String> labels = cb.setData(stepStats).setInterval(IntervalMode.WEEK, TODAY).buildTimeAxisLabel().getxAxisLabel();
        String[] tmp = {"", "09/03", "09/04", "09/05", "09/06", "09/07", "09/08", "09/09"};
        List<String> days =  new ArrayList<>(Arrays.asList(tmp));
        assertEquals(days, labels);
    }

    @Test
    public void testChartData1() {
        cb.setData(stepStats).setInterval(IntervalMode.WEEK, TODAY).buildChartData();

        BarData bd = cb.getBarData();
        LineData ld = cb.getLineData();

        BarDataSet bds = (BarDataSet)bd.getDataSetByIndex(0);
        LineDataSet lds = (LineDataSet)ld.getDataSetByIndex(0);

        // Check bar entry
        for(int i = 0; i < bds.getEntryCount(); ++i) {
            BarEntry be = bds.getEntryForIndex(i);
            float incidental = be.getYVals()[0];
            float intentional = be.getYVals()[1];
            assertEquals(stepStats.get(i + 1).getIncidentWalk(), incidental, 0.001);
            assertEquals(stepStats.get(i + 1).getIntentionalWalk(), intentional, 0.001);
        }

        // Check line entry
        for(int i = 0; i < lds.getEntryCount(); ++i) {
            Entry le = lds.getEntryForIndex(i);
            float goal = le.getY();
            assertEquals(stepStats.get(i + 1).getGoal(), goal, 0.001);
        }
    }

    @Test
    public void testChartData2() {
        cb.setData(stepStats).setInterval(IntervalMode.MONTH, TODAY).buildChartData();

        BarData bd = cb.getBarData();
        LineData ld = cb.getLineData();

        BarDataSet bds = (BarDataSet)bd.getDataSetByIndex(0);
        LineDataSet lds = (LineDataSet)ld.getDataSetByIndex(0);

        // Check bar entry
        for(int i = 0; i < bds.getEntryCount(); ++i) {
            BarEntry be = bds.getEntryForIndex(i);
            float incidental = be.getYVals()[0];
            float intentional = be.getYVals()[1];
            if ((i - 20) < 0) {
                assertEquals(0, incidental, 0.001);
                assertEquals(0, intentional, 0.001);
            }
            else{
                assertEquals(stepStats.get(i - 20).getIncidentWalk(), incidental, 0.001);
                assertEquals(stepStats.get(i - 20).getIntentionalWalk(), intentional, 0.001);
            }
        }

        // Check line entry
        for(int i = 0; i < lds.getEntryCount(); ++i) {
            Entry le = lds.getEntryForIndex(i);
            float goal = le.getY();
            if ((i - 20) < 0) {
                assertEquals(0, goal, 0.001);
            }
            else {
                assertEquals(stepStats.get(i - 20).getGoal(), goal, 0.001);
            }
        }
    }
}
