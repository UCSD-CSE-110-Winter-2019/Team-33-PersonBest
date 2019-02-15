package com.android.personbest.StepCounter;

public class DailyStat implements IStatistics {
    private int goal;
    private int totalSteps;
    private int intentionalSteps;
    private String stats;

    public DailyStat(int goal, int totalSteps, int intentionalSteps, String stats) {
        this.goal = goal;
        this.totalSteps = totalSteps;
        this.intentionalSteps = intentionalSteps;
        this.stats = stats;
    }

    public int getGoal() { return this.goal; }

    public String getStats() { return this.stats; }

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
