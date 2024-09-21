package com.asc.politicalscorecard.objects.scoring.worldview;

import java.util.Map;

public class WorldviewVote {
    // The id of the worldview vote.
    private String id;

    // The id of the worldview ballot.
    private String worldviewBallotId;

    // ----- Context-voting specific fields -----

    // The list of contexts asserted by the voter to be valid views.
    // Approval voting equivalent for contexts.
    private String[] validContexts;

    // The sole context that the voter has chosen to be voting in.
    // This is the context that the voter is asserting the worldview ballot is most valid.
    private String soleContext;

    // A ranking of the contexts asserted by the voter to be valid views.
    // Ranked voting condorcet equivalent for contexts, allows for equal rankings.
    // This should be a map of integer to context id.
    private String[] rankedContexts;

    // ----- Term-Weight Voting specific fields -----

    // The list of terms asserted by the voter to be valid.
    // Approval voting equivalent for terms.
    private String[] validTerms;

    // The map of terms to values that the voter has assigned to the terms.
    // This is a map of term id to value.
    private Map<String, Double> termValues;


}