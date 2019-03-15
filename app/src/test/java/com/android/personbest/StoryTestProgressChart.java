package com.android.personbest;

import android.app.AlertDialog;
import android.content.*;
import android.widget.Button;
import com.android.personbest.SavedDataManager.SavedDataManager;
import com.android.personbest.SavedDataManager.SavedDataManagerSharedPreference;
import com.android.personbest.StepCounter.*;
import com.android.personbest.Timer.TimerMock;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.data.CombinedData;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

import android.content.Intent;

import com.android.personbest.StepCounter.StepCounter;
import com.android.personbest.StepCounter.StepCounterFactory;
import com.android.personbest.StepCounter.StepCounterGoogleFit;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.shadows.ShadowToast;

import static org.junit.Assert.*;
import static org.robolectric.RuntimeEnvironment.application;

@RunWith(RobolectricTestRunner.class)
public class StoryTestProgressChart {
    private MainActivity mainActivity;

    @Before
    public void setup() {
        ExecMode.setExecMode(ExecMode.EMode.TEST_LOCAL);
        Intent intent = new Intent(RuntimeEnvironment.application, MainActivity.class);
        try {
            mainActivity = Robolectric.buildActivity(MainActivity.class, intent).create().get();
        } catch (Exception e) {
            mainActivity = Robolectric.buildActivity(MainActivity.class, intent).create().get();
        }
    }

    @Test
    public void testWeekIntent() {
        AlertDialog ad = mainActivity.launchProgressChart(null);
        Button week = ad.getButton(ad.BUTTON_POSITIVE);
        week.performClick();
        Intent next = Shadows.shadowOf(mainActivity).peekNextStartedActivity();
        assertEquals(ProgressChart.class.getName(), next.getComponent().getClassName());
        assertEquals("week", next.getStringExtra("mode"));
    }

    @Test
    public void testMonthIntent() {
        AlertDialog ad = mainActivity.launchProgressChart(null);
        Button week = ad.getButton(ad.BUTTON_NEUTRAL);
        week.performClick();
        Intent next = Shadows.shadowOf(mainActivity).peekNextStartedActivity();
        assertEquals(ProgressChart.class.getName(), next.getComponent().getClassName());
        assertEquals("month", next.getStringExtra("mode"));
    }

    @Test
    public void testWeekChartDisplayed() {
        Intent next = new Intent(mainActivity, ProgressChart.class);
        next.putExtra("mode", "week");
        ProgressChart pc = Robolectric.buildActivity(ProgressChart.class, next).create().get();
        CombinedChart cc = pc.findViewById(R.id.progressChart);
        assertEquals(7, cc.getData().getBarData().getEntryCount());
        assertEquals(7, cc.getData().getLineData().getEntryCount());
    }

    @Test
    public void testMonthChartDisplayed() {
        Intent next = new Intent(mainActivity, ProgressChart.class);
        next.putExtra("mode", "month");
        ProgressChart pc = Robolectric.buildActivity(ProgressChart.class, next).create().get();
        CombinedChart cc = pc.findViewById(R.id.progressChart);
        assertEquals(28, cc.getData().getBarData().getEntryCount());
        assertEquals(28, cc.getData().getLineData().getEntryCount());
    }
}
