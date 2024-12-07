package com.asc.politicalscorecard.objects.legislation;

import java.util.List;

public class Legislation {
    private String id;

    // Title of the legislation (e.g., "Clean Energy Act 2024").
    private String title;

    // Description or summary of the legislation.
    private String description;

    // Legislative body responsible for the legislation.
    private String legislativeBodyId;

    // GeoLocations where the legislation is applicable.
    private List<String> geoLocationIds;

    // Validity period for the legislation (null for ongoing laws).
    private String validFromDate;
    private String validToDate;

    // Associated policy IDs derived from this legislation.
    private List<String> associatedPolicyIds;

    // Text of the legislation (optional, for full-text analysis).
    private String legislationText;

    // Url reference to the legislation.
    private String url;
}