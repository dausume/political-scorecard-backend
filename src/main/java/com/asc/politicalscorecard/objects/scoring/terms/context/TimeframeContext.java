package com.asc.politicalscorecard.objects.scoring.terms.context;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TimeframeContext extends TermContext {
    private LocalDate startDate;
    private LocalDate endDate;

    public TimeframeContext() {
        super(TermContextType.TIMEFRAME, "");
    }

    public TimeframeContext(String label, LocalDate startDate, LocalDate endDate) {
        super(TermContextType.TIMEFRAME, label);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public String getValue() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        return startDate.format(formatter) + " - " + endDate.format(formatter);
    }

    // Getters and Setters
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "TimeframeContext{" +
                "label='" + label + '\'' +
                ", value='" + getValue() + '\'' +
                '}';
    }
}
