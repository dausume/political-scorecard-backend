package com.asc.politicalscorecard.objects.scoring.worldviewvoting;

/**
 * Defines the granularity levels for Timeframe contexts.
 * Used to constrain ballot context selection in Worldview Elections.
 */
public enum TimeframeGranularity {
    SINGLE_YEAR,   // e.g., "2024"
    YEAR_RANGE,    // e.g., "2020-2024"
    SINGLE_DATE,   // e.g., "2024-01-15"
    DATE_RANGE     // e.g., "2024-01-01 to 2024-12-31"
}
