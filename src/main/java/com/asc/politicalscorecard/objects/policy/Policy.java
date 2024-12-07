package com.asc.politicalscorecard.objects.policy;

import java.util.List;

public class Policy {
    private String id;

    // Name of the policy (e.g., "Urban Tree-Planting Initiative").
    private String name;

    // Description or purpose of the policy.
    private String description;

    // The legislation ID from which the policy is derived (optional).
    private String derivedFromLegislationId;

    // GeoLocations where the policy is implemented.
    private List<String> geoLocationIds;

    // Associated organization or entity responsible for the policy.
    private String implementingEntityId;

    // Implementation flowchart or operational document URL (if available).
    private String implementationDocumentUrl;

    // Validity period for the policy.
    private String validFromDate;
    private String validToDate;
}
