package com.asc.politicalscorecard.objects.scoring.terms.context;

import java.util.ArrayList;
import java.util.List;

public class EconomicContext extends TermContext {
    private String gdpRange;
    private String inflationRate;
    private String unemploymentRate;
    private String marketCondition;

    public EconomicContext() {
        super(TermContextType.ECONOMIC, "");
    }

    public EconomicContext(String label, String gdpRange, String inflationRate, String unemploymentRate, String marketCondition) {
        super(TermContextType.ECONOMIC, label);
        this.gdpRange = gdpRange;
        this.inflationRate = inflationRate;
        this.unemploymentRate = unemploymentRate;
        this.marketCondition = marketCondition;
    }

    @Override
    public String getValue() {
        List<String> parts = new ArrayList<>();
        if (gdpRange != null && !gdpRange.isEmpty()) parts.add("GDP: " + gdpRange);
        if (inflationRate != null && !inflationRate.isEmpty()) parts.add("Inflation: " + inflationRate);
        if (unemploymentRate != null && !unemploymentRate.isEmpty()) parts.add("Unemployment: " + unemploymentRate);
        if (marketCondition != null && !marketCondition.isEmpty()) parts.add(marketCondition);
        return String.join(", ", parts);
    }

    // Getters and Setters
    public String getGdpRange() {
        return gdpRange;
    }

    public void setGdpRange(String gdpRange) {
        this.gdpRange = gdpRange;
    }

    public String getInflationRate() {
        return inflationRate;
    }

    public void setInflationRate(String inflationRate) {
        this.inflationRate = inflationRate;
    }

    public String getUnemploymentRate() {
        return unemploymentRate;
    }

    public void setUnemploymentRate(String unemploymentRate) {
        this.unemploymentRate = unemploymentRate;
    }

    public String getMarketCondition() {
        return marketCondition;
    }

    public void setMarketCondition(String marketCondition) {
        this.marketCondition = marketCondition;
    }

    @Override
    public String toString() {
        return "EconomicContext{" +
                "label='" + label + '\'' +
                ", value='" + getValue() + '\'' +
                '}';
    }
}
