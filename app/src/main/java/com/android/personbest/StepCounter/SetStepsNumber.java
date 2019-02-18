package com.android.personbest.StepCounter;

import com.android.personbest.MainActivity;

public class SetStepsNumber {
    private MainActivity mainActivity;
    public SetStepsNumber(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
    public void setStepsNumber() {
        mainActivity.setStepCount(5001);
    }
}
