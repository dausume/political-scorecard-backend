package com.asc.politicalscorecard.objects.scoring.worldviewvoting;

/**
 * Defines the granularity levels for Location contexts.
 * Used to constrain ballot context selection in Worldview Elections.
 */
public enum LocationGranularity {
    COUNTRY,  // Only country-level contexts (e.g., "United States")
    STATE,    // State/province-level contexts (e.g., "California")
    CITY,     // City-level contexts (e.g., "San Francisco, CA")
    REGION    // Regional contexts (e.g., "Pacific Northwest")
}
