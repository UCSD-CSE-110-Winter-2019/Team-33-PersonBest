package com.android.personbest.Timer;

public class TimerMock implements ITimer {
    private int time;

    public TimerMock(int time) {
        this.time = time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTime() {
        return this.time;
    }

    @Override
    public boolean isLateToday() {
        return this.time >= 20;
    }
}
