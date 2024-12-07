package com.asc.politicalscorecard.objects.elections;

public class ElectoralPosition {
    private String id;

    // Name of the position (e.g., Governor, Mayor, State Representative)
    private String positionName;

    // The ID of the legislative body governing this position.
    private String governingLegislativeBodyId;

    // Parent position, if this position is subordinate (e.g., Mayor reports to Governor).
    private String parentPositionId;

    // Description of responsibilities.
    private String description;
}
