package com.android.personbest.StepCounter;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.personbest.MainActivity;

import java.util.HashMap;
import java.util.Map;

public class StepCounterFactory {

    private static final String TAG = "[StepCounterFactory]";

    private static Map<String, BluePrint> blueprints = new HashMap<>();

    public static void put(String key, BluePrint bluePrint) {
        blueprints.put(key, bluePrint);
    }

    public static StepCounter create(String key, MainActivity activity) {
        Log.i(TAG, String.format("creating StepCounter with key %s", key));
        return blueprints.get(key).create(activity);
    }

    public interface BluePrint {
        StepCounter create(AppCompatActivity activity);
    }
}

