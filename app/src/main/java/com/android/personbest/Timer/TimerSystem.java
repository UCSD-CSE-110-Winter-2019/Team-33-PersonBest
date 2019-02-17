package com.android.personbest.Timer;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimerSystem implements ITimer {
    public boolean isLateToday() {
        return ZonedDateTime.now(ZoneId.systemDefault()).getHour() >= 20;
    }
}
