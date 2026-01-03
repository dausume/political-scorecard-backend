package com.asc.politicalscorecard.objects.scoring.terms;

import com.asc.politicalscorecard.objects.scoring.terms.context.TermContext;
import com.asc.politicalscorecard.objects.scoring.terms.context.TermContextType;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * A ContextualizedTerm represents a Term with specific contextual data applied.
 * It contains the actual values (pre and post normalization) for the term in a given context.
 */
public class ContextualizedTerm {
    // Unique identifier for this contextualized term instance
    private String id;

    // Reference to the base term
    private Term term;

    // List of contexts that apply to this term (e.g., location, timeframe, demographic)
    private List<TermContext> contexts;

    // Metadata about the value type, unit, and interpretation
    private ValueMetadata valueMetadata;

    // The actual measured value before normalization (in original units)
    private double preNormalizedValue;

    // The normalized value (scaled to 0-1 range for scoring)
    private double postNormalizedValue;

    // Optional: Link to a Pre-Processing Polari URL (for future use)
    private String preProcessPolariUrl;

    // Optional: Link to a Post-Processing Polari URL (for future use)
    private String postProcessPolariUrl;

    // Constructors
    public ContextualizedTerm() {
    }

    public ContextualizedTerm(String id, Term term, List<TermContext> contexts,
                            ValueMetadata valueMetadata, double preNormalizedValue,
                            double postNormalizedValue) {
        this.id = id;
        this.term = term;
        this.contexts = contexts;
        this.valueMetadata = valueMetadata;
        this.preNormalizedValue = preNormalizedValue;
        this.postNormalizedValue = postNormalizedValue;
    }

    // Helper methods (matching frontend functionality)

    /**
     * Update the pre-normalized value
     */
    public void updatePreNormalizedValue(double newValue) {
        this.preNormalizedValue = newValue;
    }

    /**
     * Get formatted pre-normalized value based on value type
     */
    public String getFormattedPreNormalizedValue() {
        ValueType type = valueMetadata.getType();
        String unit = valueMetadata.getUnit();

        switch (type) {
            case PERCENTAGE:
                return preNormalizedValue + "%";
            case CURRENCY:
                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
                return (unit != null ? unit : "$") + currencyFormat.format(preNormalizedValue).replace("$", "");
            case COUNT:
                NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
                return numberFormat.format(preNormalizedValue);
            case RATE:
                return preNormalizedValue + (unit != null ? unit : "");
            default:
                return unit != null ? preNormalizedValue + " " + unit : String.valueOf(preNormalizedValue);
        }
    }

    /**
     * Get context by type
     */
    public TermContext getContextByType(TermContextType type) {
        return contexts.stream()
                .filter(c -> c.getType() == type)
                .findFirst()
                .orElse(null);
    }

    /**
     * Check if has context of specific type
     */
    public boolean hasContextType(TermContextType type) {
        return contexts.stream().anyMatch(c -> c.getType() == type);
    }

    /**
     * Get context summary string
     */
    public String getContextSummary() {
        return contexts.stream()
                .map(c -> c.getLabel() + ": " + c.getValue())
                .collect(Collectors.joining(" | "));
    }

    /**
     * Get full description including context
     */
    public String getFullDescription() {
        String contextSummary = getContextSummary();
        return contextSummary.isEmpty()
                ? term.getName()
                : term.getName() + " (" + contextSummary + ")";
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public List<TermContext> getContexts() {
        return contexts;
    }

    public void setContexts(List<TermContext> contexts) {
        this.contexts = contexts;
    }

    public ValueMetadata getValueMetadata() {
        return valueMetadata;
    }

    public void setValueMetadata(ValueMetadata valueMetadata) {
        this.valueMetadata = valueMetadata;
    }

    public double getPreNormalizedValue() {
        return preNormalizedValue;
    }

    public void setPreNormalizedValue(double preNormalizedValue) {
        this.preNormalizedValue = preNormalizedValue;
    }

    public double getPostNormalizedValue() {
        return postNormalizedValue;
    }

    public void setPostNormalizedValue(double postNormalizedValue) {
        this.postNormalizedValue = postNormalizedValue;
    }

    public String getPreProcessPolariUrl() {
        return preProcessPolariUrl;
    }

    public void setPreProcessPolariUrl(String preProcessPolariUrl) {
        this.preProcessPolariUrl = preProcessPolariUrl;
    }

    public String getPostProcessPolariUrl() {
        return postProcessPolariUrl;
    }

    public void setPostProcessPolariUrl(String postProcessPolariUrl) {
        this.postProcessPolariUrl = postProcessPolariUrl;
    }

    @Override
    public String toString() {
        return "ContextualizedTerm{" +
                "id='" + id + '\'' +
                ", term=" + (term != null ? term.getName() : "null") +
                ", value=" + getFormattedPreNormalizedValue() +
                " -> " + String.format("%.4f", postNormalizedValue) + " [0-1]" +
                ", contexts=" + contexts.size() +
                '}';
    }
}

