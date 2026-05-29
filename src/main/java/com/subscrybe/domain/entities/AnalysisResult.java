package com.subscrybe.domain.entities;

public class AnalysisResult {

    private final String recommendation; // KEEP, REDUCE, CANCEL
    private final String reason;
    private final double monthlyCost;
    private final double costPerSession;
    private final int daysUsedPerWeek;

    public AnalysisResult(String recommendation, String reason,
                          double monthlyCost, double costPerSession, int daysUsedPerWeek) {
        this.recommendation = recommendation;
        this.reason = reason;
        this.monthlyCost = monthlyCost;
        this.costPerSession = costPerSession;
        this.daysUsedPerWeek = daysUsedPerWeek;
    }

    public String getRecommendation() { return recommendation; }
    public String getReason()         { return reason; }
    public double getMonthlyCost()    { return monthlyCost; }
    public double getCostPerSession() { return costPerSession; }
    public int getDaysUsedPerWeek()   { return daysUsedPerWeek; }
}
