package com.android.personbest.StepCounter;

import java.util.Date;

public class DateCalendar implements IDate {

    private int day;

    public DateCalendar (){
        Date now = new Date();
        this.day = now.getDay();
    }

    public DateCalendar(int day){
        this.day = day;
    }

    @Override
    public int getYesterDay() {
        if (this.day == 0){
            return FINAL_DAY;
        }
        else{
            return this.day - 1;
        }
    }

    @Override
    public int getDay() {
        return this.day;
    }
}
