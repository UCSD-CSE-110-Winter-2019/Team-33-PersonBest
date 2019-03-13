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

import static org.junit.Assert.assertEquals;

public class TestProgressChart {
    SavedDataManager manager;
    private static final boolean dummy = ExecMode.setExecMode(ExecMode.EMode.TEST_LOCAL);

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

    /* This test checks if the data that is put into the chart is correct. It does by getting data sets from the final
     * data, therefore also implicitly checks the correctness of the method that generates the data sets
     */
    @Test
    public void testSetUpChart2() {
        // Day of week strings
        final String[] week = {DayOfWeek.of(7).getDisplayName(TextStyle.SHORT, Locale.US),
                               DayOfWeek.of(1).getDisplayName(TextStyle.SHORT, Locale.US),
                               DayOfWeek.of(2).getDisplayName(TextStyle.SHORT, Locale.US),
                               DayOfWeek.of(3).getDisplayName(TextStyle.SHORT, Locale.US),
                               DayOfWeek.of(4).getDisplayName(TextStyle.SHORT, Locale.US),
                               DayOfWeek.of(5).getDisplayName(TextStyle.SHORT, Locale.US),
                               DayOfWeek.of(6).getDisplayName(TextStyle.SHORT, Locale.US)
                              };

        // Set different date
        int date = 7;
        List<IStatistics> stepStats = manager.getLastWeekSteps("03/02/2019", null);
        progressChart.setBarChart(stepStats);

        // Get data
        BarData bd = progressChart.getBarData();
        LineData ld = progressChart.getLineData();
        ArrayList<String> xAxisLabel = progressChart.getXAxisLabel();
        String dailyStats = progressChart.createStatsStr(stepStats);

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
        assertEquals(7,xAxisLabel.size() - 1);
        for(int i = 1; i < xAxisLabel.size(); ++i) {
            assertEquals(week[i - 1], xAxisLabel.get(i));
        }

        // Check Daily stats
        String correctStats = "Intentional Work Statistics\n" +
                "Sun: Steps:  3255 Dist: 1.3mi Time: 0.3 hrs MPH: 4.0\n" +
                "Mon: Steps:  3752 Dist: 1.3mi Time: 0.3 hrs MPH: 4.0\n" +
                "Tue: Steps:   882 Dist: 1.3mi Time: 0.3 hrs MPH: 4.0\n" +
                "Wed: Steps:  3078 Dist: 1.3mi Time: 0.3 hrs MPH: 4.0\n" +
                "Thu: Steps:  3673 Dist: 1.3mi Time: 0.3 hrs MPH: 4.0\n" +
                "Fri: Steps:  9225 Dist: 1.3mi Time: 0.3 hrs MPH: 4.0\n" +
                "Sat: Steps:  6706 Dist: 1.3mi Time: 0.3 hrs MPH: 4.0\n";
        assertEquals(correctStats, dailyStats);
    }

    /* Not using for now
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
    }*/
}
