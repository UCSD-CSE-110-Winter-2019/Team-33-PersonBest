package com.android.personbest.StepCounter;

import java.util.Calendar;

public class DateCalendar implements IDate {

    private int day;
    private Calendar cal;

    public DateCalendar (){
        this.day = -1;
        cal = Calendar.getInstance();
    }

    public DateCalendar(int day){
        this.day = day;
    }

    @Override
    public int getYesterday() {
        int realDay = -1;
        if(this.day == -1) realDay = cal.get(Calendar.DAY_OF_WEEK);

        if (this.day == 1 || realDay == 1){
            return FINAL_DAY;
        } else if (this.day != -1){
            return this.day - 1;
        } else {
            if(realDay == -1) {
                throw new IllegalStateException("realDay is not properly set with non-constructor");
            }
            return realDay - 1;
        }
    }

    @Override
    public int getDay() {
        if(day == -1) return cal.get(Calendar.DAY_OF_WEEK);
        else return this.day;
    }
}
