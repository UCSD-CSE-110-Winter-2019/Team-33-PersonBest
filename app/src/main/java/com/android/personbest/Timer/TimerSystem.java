package com.android.personbest.Timer;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;

public class TimerSystem extends ITimer {
    public boolean isLateToday() {
        return ZonedDateTime.now(ZoneId.systemDefault()).getHour() >= 20;
    }

    public String getTodayString() {
       return DateTimeFormatter.ofPattern("MM/dd/yyyy").format(ZonedDateTime.now());
    }

    public String getYesterdayString() {
        return DateTimeFormatter.ofPattern("MM/dd/yyyy").format(ZonedDateTime.now().plusDays(-1));
    }
}
