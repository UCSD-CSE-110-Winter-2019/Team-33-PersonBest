package com.android.personbest;

import com.android.personbest.SavedDataManager.SavedDataManager;
import com.android.personbest.SavedDataManager.SavedDataManagerTest;
import com.android.personbest.StepCounter.DailyStat;
import com.android.personbest.StepCounter.IStatistics;
import com.github.mikephil.charting.data.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import android.support.test.rule.ActivityTestRule;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

public class TestProgressChart {
    SavedDataManager manager;

    @Rule
    public ActivityTestRule<ProgressChart> progressChartTestRule = new ActivityTestRule<>(ProgressChart.class);
    private ProgressChart progressChart;

    @Before
    public void setUp() {

        // Mock data
        List<IStatistics> stepStats = new ArrayList<>();
        stepStats.add(new DailyStat(5000, 8226, 3255, 1200000, 4.0f));
        stepStats.add(new DailyStat(5540, 6260, 3752, 1200000, 4.0f));
        stepStats.add(new DailyStat(6000, 1939, 882, 1200000, 4.0f));
        stepStats.add(new DailyStat(6500, 3755, 3078, 1200000, 4.0f));
        stepStats.add(new DailyStat(6875, 4530, 3673, 1200000, 4.0f));
        stepStats.add(new DailyStat(7500, 9934, 9225, 1200000, 4.0f));
        stepStats.add(new DailyStat(8500, 7633, 6706, 1200000, 4.0f));
        manager = new SavedDataManagerTest(stepStats);

        // Get activity
        progressChart = progressChartTestRule.getActivity();
        progressChart.setManager(manager);
    }

    @Test
    public void testSetUpChart() {
        // Day of week strings
        final String[] week = {DayOfWeek.of(7).getDisplayName(TextStyle.SHORT, Locale.US),
                               DayOfWeek.of(1).getDisplayName(TextStyle.SHORT, Locale.US),
                               DayOfWeek.of(2).getDisplayName(TextStyle.SHORT, Locale.US),
                               DayOfWeek.of(3).getDisplayName(TextStyle.SHORT, Locale.US),
                               DayOfWeek.of(4).getDisplayName(TextStyle.SHORT, Locale.US),
                               DayOfWeek.of(5).getDisplayName(TextStyle.SHORT, Locale.US),
                               DayOfWeek.of(6).getDisplayName(TextStyle.SHORT, Locale.US)
                              };

        int date = 1;
        List<IStatistics> stepStats = manager.getLastWeekSteps(date);
        progressChart.setBarChart(stepStats);

        // Get data
        BarData bd = progressChart.getBarData();
        LineData ld = progressChart.getLineData();
        ArrayList<String> xAxisLabel = progressChart.getXAxisLabel();
        ArrayList<String> dailyStats = progressChart.getDailyStats();

        BarDataSet bds = (BarDataSet)bd.getDataSetByIndex(0);
        LineDataSet lds = (LineDataSet)ld.getDataSetByIndex(0);

        // Check bar entry
        for(int i = 0; i < bds.getEntryCount(); ++i) {
            BarEntry be = bds.getEntryForIndex(i);
            float incidental = be.getYVals()[0];
            float intentional = be.getYVals()[1];
            assertEquals(stepStats.get(i).getIncidentWalk(), incidental, 0.001);
            assertEquals(stepStats.get(i).getIntentionalWalk(), intentional, 0.001);
        }

        // Check line entry
        for(int i = 0; i < lds.getEntryCount(); ++i) {
            Entry le = lds.getEntryForIndex(i);
            float goal = le.getY();
            assertEquals(stepStats.get(i).getGoal(), goal, 0.001);
        }

        // Check x-axis label
        assertEquals(1,xAxisLabel.size() - 1);
        for(int i = 1; i < xAxisLabel.size(); ++i) {
            assertEquals(week[i - 1], xAxisLabel.get(i));
        }

        // Check Daily stats
        assertEquals(1, dailyStats.size());
        for(int i = 0; i < dailyStats.size(); ++i) {
            assertEquals(stepStats.get(i).getStats(), dailyStats.get(i));
        }

        // Set different date
        date = 7;
        stepStats = manager.getLastWeekSteps(date);
        progressChart.setBarChart(stepStats);

        // Get data
        bd = progressChart.getBarData();
        ld = progressChart.getLineData();
        xAxisLabel = progressChart.getXAxisLabel();
        dailyStats = progressChart.getDailyStats();

        bds = (BarDataSet)bd.getDataSetByIndex(0);
        lds = (LineDataSet)ld.getDataSetByIndex(0);

        // Check bar entry
        for(int i = 0; i < bds.getEntryCount(); ++i) {
            BarEntry be = bds.getEntryForIndex(i);
            float incidental = be.getYVals()[0];
            float intentional = be.getYVals()[1];
            assertEquals(stepStats.get(i).getIncidentWalk(), incidental, 0.001);
            assertEquals(stepStats.get(i).getIntentionalWalk(), intentional, 0.001);
        }

        // Check line entry
        for(int i = 0; i < lds.getEntryCount(); ++i) {
            Entry le = lds.getEntryForIndex(i);
            float goal = le.getY();
            assertEquals(stepStats.get(i).getGoal(), goal, 0.001);
        }

        // Check x-axis label
        assertEquals(7,xAxisLabel.size() - 1);
        for(int i = 1; i < xAxisLabel.size(); ++i) {
            assertEquals(week[i - 1], xAxisLabel.get(i));
        }

        // Check Daily stats
        assertEquals(7, dailyStats.size());
        for(int i = 0; i < dailyStats.size(); ++i) {
            assertEquals(stepStats.get(i).getStats(), dailyStats.get(i));
        }
    }

    @Test
    public void testValueFormatter() {
        // Get strings
        List<IStatistics> stats = manager.getLastWeekSteps(7);
        List<String> appendices = new ArrayList<>();
        for(IStatistics stat: stats) {
            appendices.add(stat.getStats());
        }
        ProgressChart.EnhancedStackedValueFormatter f =
                new ProgressChart.EnhancedStackedValueFormatter(appendices.toArray(new String[appendices.size()]));

        // Test
        for(int i = 1; i <= 7; ++i) {
            assertEquals(stats.get(i - 1).getStats(), f.getFormattedValue(0, new BarEntry(i, new float[]{0}), 0, null));
            assertEquals("", f.getFormattedValue(1, new BarEntry(i, new float[]{0}), 0, null));
        }
    }
}
