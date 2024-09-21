package com.asc.politicalscorecard.objects.scoring.competitors;

// A dimension of competitors in a scoring system in relation to a specific context.
// Each competitor is expected to meet all conditions such that they are appropriate to be accounted for in
// the scoring system for the given context.
public class CompetitorDimension {
    // The id of the dimension.
    private String dimensionId;

    // The id of all competitors that meet the conditions of this dimension.
    private String[] competitors;
}
