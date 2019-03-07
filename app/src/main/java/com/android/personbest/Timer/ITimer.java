package com.android.personbest.Timer;

import android.support.annotation.NonNull;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public abstract class ITimer {
    private static final int DATE_STRING_LENGTH = 10;
    protected static final int LAST_MONTH_DAYS = 28;
    protected static final int LAST_WEEK_DAYS = 7;

    public static boolean isValidDayStr(@NonNull String day) {
        try {
            if(day.length() != DATE_STRING_LENGTH) {
                return false;
            }
            int mm = Integer.parseInt(day.substring(0,2));
            int dd = Integer.parseInt(day.substring(3,5));
            int yyyy = Integer.parseInt(day.substring(6,DATE_STRING_LENGTH));
            return (1 <= mm && mm <= 12 && 1 <= dd && dd <= 31 && 0 < yyyy);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getYearFromDayStr(String dayStr) {
        return Integer.parseInt(dayStr.substring(6,DATE_STRING_LENGTH));
    }

    public static int getMonthFromDayStr(String dayStr) {
        return  Integer.parseInt(dayStr.substring(0,2));
    }

    public static int getDayFromDayStr(String dayStr) {
        return Integer.parseInt(dayStr.substring(3,5));
    }

    public static List<String> getLastWeekDayStrings(String day) {
        if(!isValidDayStr(day)) throw new IllegalArgumentException("argument day in day wrong format");

        ArrayList<String> toReturn = new ArrayList<>();
        for(int i = 1; i <= ITimer.LAST_WEEK_DAYS; ++i) {
            toReturn.add(DateTimeFormatter.ofPattern("MM-dd-yyyy").format(
                    ZonedDateTime.of(
                        getYearFromDayStr(day),
                        getMonthFromDayStr(day),
                        getDayFromDayStr(day),
                        1,1,1,1, ZonedDateTime.now().getZone()
                    ).minusDays(i)
            ));
        }
        return toReturn;
    }

    public static List<String> getLastMonthDayStrings(String day) {
        if(!isValidDayStr(day)) throw new IllegalArgumentException("argument day in day wrong format");

        ArrayList<String> toReturn = new ArrayList<>();
        for(int i = 1; i <= ITimer.LAST_MONTH_DAYS; ++i) {
            toReturn.add(DateTimeFormatter.ofPattern("MM-dd-yyyy").format(
                    ZonedDateTime.of(
                        getYearFromDayStr(day),
                        getMonthFromDayStr(day),
                        getDayFromDayStr(day),
                        1,1,1,1, ZonedDateTime.now().getZone()
                    ).minusDays(i)
            ));
        }
        return toReturn;
    }

    public static String getDayStrDayBefore(String day, int n) {
        if(!isValidDayStr(day)) throw new IllegalArgumentException("argument day in day wrong format");
        return (DateTimeFormatter.ofPattern("MM-dd-yyyy").format(
                ZonedDateTime.of(
                    getYearFromDayStr(day),
                    getMonthFromDayStr(day),
                    getDayFromDayStr(day),
                    1,1,1,1, ZonedDateTime.now().getZone()
                ).minusDays(n)
        ));
    }

    public static int getDayStamp(String day) {
        if(!isValidDayStr(day)) throw new IllegalArgumentException("argument day in day wrong format");
        return Integer.parseInt((DateTimeFormatter.ofPattern("yyyyMMdd").format(
                ZonedDateTime.of(
                    getYearFromDayStr(day),
                    getMonthFromDayStr(day),
                    getDayFromDayStr(day),
                    1,1,1,1, ZonedDateTime.now().getZone()
                )
        )));
    }

    public static int getDayStampDayBefore(String day, int n) {
        if(!isValidDayStr(day)) throw new IllegalArgumentException("argument day in day wrong format");
        return Integer.parseInt((DateTimeFormatter.ofPattern("yyyyMMdd").format(
                ZonedDateTime.of(
                    getYearFromDayStr(day),
                    getMonthFromDayStr(day),
                    getDayFromDayStr(day),
                    1,1,1,1, ZonedDateTime.now().getZone()
                ).minusDays(n)
        )));
    }

    public static int getDayStampWeekBefore(String day) {
        return getDayStampDayBefore(day, LAST_WEEK_DAYS);
    }

    public static int getDayStampMonthBefore(String day) {
        return getDayStampDayBefore(day, LAST_MONTH_DAYS);
    }

    public abstract boolean isLateToday();
    public abstract String getTodayString();
    public abstract String getYesterdayString();
}
