package com.android.personbest.Timer;

public class TimerMock extends ITimer {
    private int time;
    private String todayStr;
    private String yesterdayStr;

    public TimerMock(int time, String todayStr, String yesterdayStr) {
        this.time = time;
        this.todayStr = todayStr;
        this.yesterdayStr = yesterdayStr;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTime() {
        return this.time;
    }

    public void setTodayStr(String todayStr) {
        this.todayStr = todayStr;
    }

    public String getTodayStr() {
        return this.todayStr;
    }

    public void setYesterdayStr(String yesterdayStr) {
        this.yesterdayStr = yesterdayStr;
    }

    public String getYesterdayStr() {
        return this.yesterdayStr;
    }

    @Override
    public boolean isLateToday() {
        return this.time >= 20;
    }

    public String getTodayString() {
        return this.todayStr;
    }

    public String getYesterdayString() {
        return this.yesterdayStr;
    }
}
