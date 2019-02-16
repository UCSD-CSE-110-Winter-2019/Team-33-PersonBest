package com.android.personbest.StepCounter;

public class IDate {
    private final int FINAL_DAY = 6;
    private int day;
    public IDate(int day){
        this.day = day;
    }

    public int getYesterDay(){
        if (this.day == 0){
            return FINAL_DAY;
        }
        else{
            return this.day - 1;
        }
    }

    public int getDay(){
        return this.day;
    }
}
