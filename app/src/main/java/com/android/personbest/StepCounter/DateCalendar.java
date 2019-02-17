package com.android.personbest.StepCounter;

import java.util.Calendar;

public class DateCalendar implements IDate {

    private int day;

    public DateCalendar (){
        this.day = -1;
    }

    public DateCalendar(int day){
        this.day = day;
    }

    @Override
    public int getYesterday() {
        if (this.day == 0){
            return FINAL_DAY;
        }
        else{
            return this.day - 1;
        }
    }

    @Override
    public int getDay() {
        if(day == -1) return Calendar.DAY_OF_WEEK;
        else return this.day;
    }
}
