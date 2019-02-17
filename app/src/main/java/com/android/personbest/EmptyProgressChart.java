package com.android.personbest;

import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.security.InvalidParameterException;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EmptyProgressChart extends AppCompatActivity {

    private List<BarEntry> entries;
    private SavedDataManager savedDataManager;
    private int date;
    private BarChart progressChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_progress_chart);

        entries = new ArrayList<>();
        savedDataManager = new SavedDataManagerSharedPreference(this);

        progressChart = findViewById(R.id.progressChart);

        setDate(ZonedDateTime.now(ZoneId.systemDefault()).getDayOfWeek().getValue() - 1);
    }

    public void setDate(int date) {
        if(date < 0 || date >= 7)
            throw new InvalidParameterException("Date expected between 0 and 6, but got" + date);
        this.date = date;
        setBarChart();
    }

    public void setBarChart() {
        entries.clear();
        List<IStatistics> stepStats = new ArrayList<>();//stepCounter.getLastWeekSteps(date);
        ArrayList<String> xAxis = new ArrayList<>();
        ArrayList<LegendEntry> legendEntries = new ArrayList<>();
        xAxis.add("");


        int i = 0, j = 6;
        for (IStatistics stat : stepStats) {
            i += 1;
            j = j % 7;
            float[] tempArr = new float[2];
            tempArr[0] = stat.getIncidentWalk();
            tempArr[1] = stat.getIntentionalWalk();
            //tempArr[2] = stat.getGoal();
            entries.add(new BarEntry(i, tempArr));
            xAxis.add(DayOfWeek.of(++j).getDisplayName(TextStyle.SHORT, Locale.US));
        }
        BarDataSet stepDataSet = new BarDataSet(entries, "Steps Current Week");
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
        progressChart.invalidate();
    }
}
