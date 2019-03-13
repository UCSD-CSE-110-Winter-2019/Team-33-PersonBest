package com.android.personbest;

import android.graphics.Color;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public final class ProgressUtils {
    public enum IntervalMode {
        WEEK,
        MONTH
    }

    public static List<Pair<String, Integer>> createLegendEntries() {

        // Create legend entries
        List<Pair<String, Integer>> entries = new ArrayList<>();
        entries.add(new Pair<>("Incidental Walk", Color.rgb(0, 92, 175)));
        entries.add(new Pair<>("Intentional Walk", Color.rgb(123, 144, 210)));
        return entries;
    }
    // TODO more axils and legend stuff here
}
