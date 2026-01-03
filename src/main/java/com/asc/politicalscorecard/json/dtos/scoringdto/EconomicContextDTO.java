package com.asc.politicalscorecard.json.dtos.scoringdto;

import com.asc.politicalscorecard.objects.scoring.terms.context.EconomicContext;
import com.asc.politicalscorecard.objects.scoring.terms.context.TermContextType;

/**
 * DTO for Economic Context.
 * Supports economic conditions: GDP, inflation, unemployment, market conditions.
 */
public class EconomicContextDTO extends TermContextDTO {

    private String gdpRange;
    private String inflationRate;
    private String unemploymentRate;
    private String marketCondition;

    // Constructors
    public EconomicContextDTO() {
        super(TermContextType.ECONOMIC, "");
    }

    public EconomicContextDTO(String label, String gdpRange, String inflationRate,
                              String unemploymentRate, String marketCondition) {
        super(TermContextType.ECONOMIC, label);
        this.gdpRange = gdpRange;
        this.inflationRate = inflationRate;
        this.unemploymentRate = unemploymentRate;
        this.marketCondition = marketCondition;
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
    public String getValue() {
        StringBuilder sb = new StringBuilder();
        if (gdpRange != null) sb.append("GDP: ").append(gdpRange);
        if (inflationRate != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("Inflation: ").append(inflationRate);
        }
        if (unemploymentRate != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("Unemployment: ").append(unemploymentRate);
        }
        if (marketCondition != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(marketCondition);
        }
        return sb.toString();
    }

    @Override
    public EconomicContext toEntity() {
        EconomicContext context = new EconomicContext();
        context.setId(this.getId());
        context.setLabel(this.getLabel());
        context.setGdpRange(this.gdpRange);
        context.setInflationRate(this.inflationRate);
        context.setUnemploymentRate(this.unemploymentRate);
        context.setMarketCondition(this.marketCondition);
        return context;
    }

    public static EconomicContextDTO fromEntity(EconomicContext context) {
        if (context == null) {
            return null;
        }
        EconomicContextDTO dto = new EconomicContextDTO();
        dto.setId(context.getId());
        dto.setLabel(context.getLabel());
        dto.setGdpRange(context.getGdpRange());
        dto.setInflationRate(context.getInflationRate());
        dto.setUnemploymentRate(context.getUnemploymentRate());
        dto.setMarketCondition(context.getMarketCondition());
        return dto;
    }

    @Override
    public String toString() {
        return "EconomicContextDTO{" +
                "id='" + getId() + '\'' +
                ", label='" + getLabel() + '\'' +
                ", gdpRange='" + gdpRange + '\'' +
                ", inflationRate='" + inflationRate + '\'' +
                ", unemploymentRate='" + unemploymentRate + '\'' +
                ", marketCondition='" + marketCondition + '\'' +
                '}';
    }
}
