package com.eskisehir.eventapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Multi-criteria score breakdown for a route.
 * All scores normalized to [0, 1].
 */
public class RouteScoreBreakdown {
    
    @JsonProperty("preference_fit")
    private Double preferenceFit;
    
    @JsonProperty("crowd_avoidance")
    private Double crowdAvoidance;
    
    @JsonProperty("budget_efficiency")
    private Double budgetEfficiency;
    
    @JsonProperty("sustainability")
    private Double sustainability;
    
    @JsonProperty("local_support")
    private Double localSupport;
    
    @JsonProperty("diversity")
    private Double diversity;
    
    @JsonProperty("final_composite_score")
    private Double finalCompositeScore;
    
    public RouteScoreBreakdown() {
    }
    
    public RouteScoreBreakdown(
        Double preferenceFit,
        Double crowdAvoidance,
        Double budgetEfficiency,
        Double sustainability,
        Double localSupport,
        Double diversity,
        Double finalCompositeScore
    ) {
        this.preferenceFit = preferenceFit;
        this.crowdAvoidance = crowdAvoidance;
        this.budgetEfficiency = budgetEfficiency;
        this.sustainability = sustainability;
        this.localSupport = localSupport;
        this.diversity = diversity;
        this.finalCompositeScore = finalCompositeScore;
    }
    
    // Getters and setters
    public Double getPreferenceFit() {
        return preferenceFit;
    }
    
    public void setPreferenceFit(Double preferenceFit) {
        this.preferenceFit = preferenceFit;
    }
    
    public Double getCrowdAvoidance() {
        return crowdAvoidance;
    }
    
    public void setCrowdAvoidance(Double crowdAvoidance) {
        this.crowdAvoidance = crowdAvoidance;
    }
    
    public Double getBudgetEfficiency() {
        return budgetEfficiency;
    }
    
    public void setBudgetEfficiency(Double budgetEfficiency) {
        this.budgetEfficiency = budgetEfficiency;
    }
    
    public Double getSustainability() {
        return sustainability;
    }
    
    public void setSustainability(Double sustainability) {
        this.sustainability = sustainability;
    }
    
    public Double getLocalSupport() {
        return localSupport;
    }
    
    public void setLocalSupport(Double localSupport) {
        this.localSupport = localSupport;
    }
    
    public Double getDiversity() {
        return diversity;
    }
    
    public void setDiversity(Double diversity) {
        this.diversity = diversity;
    }
    
    public Double getFinalCompositeScore() {
        return finalCompositeScore;
    }
    
    public void setFinalCompositeScore(Double finalCompositeScore) {
        this.finalCompositeScore = finalCompositeScore;
    }
}
