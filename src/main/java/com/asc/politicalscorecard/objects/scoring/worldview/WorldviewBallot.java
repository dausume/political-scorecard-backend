package com.asc.politicalscorecard.objects.scoring.worldview;

import com.asc.politicalscorecard.objects.scoring.competitors.CompetitorDimension;
import com.asc.politicalscorecard.objects.scoring.terms.Term;

// A worldview ballot is a set of terms, competitors, contexts, that are the template for a ballot
// that a user can fill out to score competitors.
public class WorldviewBallot {
    // The id of the worldview ballot.
    private String id;

    // The name of the worldview ballot.
    private String name;

    // The id of the user that created the worldview ballot.
    private String userId;

    // The id of the category that the worldview ballot is associated with.
    private String categoryId;

    // The terms that are part of the worldview ballot.
    private Term[] terms;

    // The competitors that are part of the worldview ballot.
    private CompetitorDimension[] competitors;

    // The contexts that are part of the worldview ballot, a voter may choose from a selection of contexts to score 
    // competitors using the worldview ballot.  This is to allow for people with significantly different worldviews to
    // score competitors in a way that is meaningful to them in a democratic system with a diverse population.
    private String[] contexts;
}
