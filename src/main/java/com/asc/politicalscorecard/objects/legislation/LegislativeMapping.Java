package com.asc.politicalscorecard.objects.legislation;

import java.util.List;

public class LegislativeMapping {
    private String id;

    // Associated Legislative Body ID (e.g., State Senate, Congressional District)
    private String legislativeBodyId;

    // Associated Electoral Position ID (e.g., Governor, Senator)
    private String electoralPositionId;

    // List of GeoLocation IDs the legislative mapping applies to.
    private List<String> geoLocationIds;

    // The date this mapping is valid from (used for historical districts).
    private String validFromDate;

    // The date this mapping is valid to (null for ongoing).
    private String validToDate;
}