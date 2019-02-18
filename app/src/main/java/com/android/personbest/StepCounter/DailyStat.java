package com.android.personbest.StepCounter;

public class DailyStat implements IStatistics {
    private int goal;
    private int totalSteps;
    private int intentionalSteps;
    private Long timeWalked;
    private float averageMPH;
    private float hours;
    public static final long MILLISECONDS_IN_A_MINUTE = 60000;

    public DailyStat(int goal, int totalSteps, int intentionalSteps, long timeWalked, float averageMPH) {
        this.goal = goal;
        this.totalSteps = totalSteps;
        this.intentionalSteps = intentionalSteps;
        this.timeWalked = timeWalked;
        this.averageMPH = averageMPH;
        this.hours = timeWalked/((float)MILLISECONDS_IN_A_MINUTE*60L);
    }

    public int getGoal() { return this.goal; }

    public String getStats() {
        //Float distance = this.hours * this.averageMPH;
        String stats = String.format("Steps: %5d", intentionalSteps) +  String.format(" Dist: %3.1f", averageMPH * hours) +
                       String.format("mi Time: %3.1f", hours) + String.format(" hrs MPH: %3.1f", averageMPH);
        return stats;
    }

    public void addIntentionalWalkSteps(int steps) {
        this.intentionalSteps += steps;
        addToTotalSteps(steps);
    }

    public void addToTotalSteps(int steps) {
        this.totalSteps += steps;
    }

    public int getIntentionalWalk() {
        return this.intentionalSteps;
    }

    public int getIncidentWalk() {
        return this.totalSteps - this.intentionalSteps;
    }
}
