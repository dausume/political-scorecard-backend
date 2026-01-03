package com.asc.politicalscorecard.json.dtos.scoringdto;

import com.asc.politicalscorecard.objects.scoring.terms.context.TermContextType;
import com.asc.politicalscorecard.objects.scoring.terms.context.TimeframeContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * DTO for Timeframe Context.
 * Supports:
 * - Singular years (2022)
 * - Year ranges (2024-2028)
 * - Specific dates
 * - Date ranges
 */
public class TimeframeContextDTO extends TermContextDTO {

    private LocalDate startDate;
    private LocalDate endDate;
    private TimeframeType timeframeType;

    public enum TimeframeType {
        SINGLE_YEAR,      // 2022
        YEAR_RANGE,       // 2024-2028
        SINGLE_DATE,      // 2024-01-15
        DATE_RANGE        // 2024-01-01 to 2024-12-31
    }

    // Constructors
    public TimeframeContextDTO() {
        super(TermContextType.TIMEFRAME, "");
    }

    public TimeframeContextDTO(String label, LocalDate startDate, LocalDate endDate, TimeframeType timeframeType) {
        super(TermContextType.TIMEFRAME, label);
        this.startDate = startDate;
        this.endDate = endDate;
        this.timeframeType = timeframeType;
    }

    // Convenience constructors for different timeframe types

    /**
     * Create a single year context (e.g., 2022)
     */
    public static TimeframeContextDTO forYear(int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        return new TimeframeContextDTO(String.valueOf(year), start, end, TimeframeType.SINGLE_YEAR);
    }

    /**
     * Create a year range context (e.g., 2024-2028)
     */
    public static TimeframeContextDTO forYearRange(int startYear, int endYear) {
        LocalDate start = LocalDate.of(startYear, 1, 1);
        LocalDate end = LocalDate.of(endYear, 12, 31);
        return new TimeframeContextDTO(startYear + "-" + endYear, start, end, TimeframeType.YEAR_RANGE);
    }

    /**
     * Create a specific date context
     */
    public static TimeframeContextDTO forDate(LocalDate date) {
        return new TimeframeContextDTO(date.toString(), date, date, TimeframeType.SINGLE_DATE);
    }

    /**
     * Create a date range context
     */
    public static TimeframeContextDTO forDateRange(LocalDate startDate, LocalDate endDate) {
        return new TimeframeContextDTO(
            startDate + " to " + endDate,
            startDate,
            endDate,
            TimeframeType.DATE_RANGE
        );
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

    public TimeframeType getTimeframeType() {
        return timeframeType;
    }

    public void setTimeframeType(TimeframeType timeframeType) {
        this.timeframeType = timeframeType;
    }

    @Override
    public String getValue() {
        DateTimeFormatter formatter;
        switch (timeframeType) {
            case SINGLE_YEAR:
            case YEAR_RANGE:
                return getLabel();
            case SINGLE_DATE:
            case DATE_RANGE:
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                if (startDate.equals(endDate)) {
                    return startDate.format(formatter);
                }
                return startDate.format(formatter) + " to " + endDate.format(formatter);
            default:
                return getLabel();
        }
    }

    @Override
    public TimeframeContext toEntity() {
        TimeframeContext context = new TimeframeContext();
        context.setId(this.getId());
        context.setLabel(this.getLabel());
        context.setStartDate(this.startDate);
        context.setEndDate(this.endDate);
        return context;
    }

    public static TimeframeContextDTO fromEntity(TimeframeContext context) {
        if (context == null) {
            return null;
        }
        TimeframeContextDTO dto = new TimeframeContextDTO();
        dto.setId(context.getId());
        dto.setLabel(context.getLabel());
        dto.setStartDate(context.getStartDate());
        dto.setEndDate(context.getEndDate());
        // Determine timeframe type based on dates
        dto.setTimeframeType(determineTimeframeType(context.getStartDate(), context.getEndDate()));
        return dto;
    }

    private static TimeframeType determineTimeframeType(LocalDate start, LocalDate end) {
        // Simple heuristic to determine type
        if (start.getDayOfYear() == 1 && end.getDayOfYear() == 365 && start.getYear() == end.getYear()) {
            return TimeframeType.SINGLE_YEAR;
        } else if (start.getDayOfYear() == 1 && end.getDayOfYear() == 365 && start.getYear() != end.getYear()) {
            return TimeframeType.YEAR_RANGE;
        } else if (start.equals(end)) {
            return TimeframeType.SINGLE_DATE;
        } else {
            return TimeframeType.DATE_RANGE;
        }
    }

    @Override
    public String toString() {
        return "TimeframeContextDTO{" +
                "id='" + getId() + '\'' +
                ", label='" + getLabel() + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", timeframeType=" + timeframeType +
                '}';
    }
}
