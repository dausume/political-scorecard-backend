package com.asc.politicalscorecard.objects.scoring;

import java.util.ArrayList;

import com.asc.politicalscorecard.objects.scoring.competitors.Competitor;
import com.asc.politicalscorecard.objects.scoring.context.Context;
import com.asc.politicalscorecard.objects.scoring.worldview.WorldviewVote;

// Uses a set of Terms, Competitors, Contexts, and Term Weightings from Worldview ballots to calculate a score.
public class Score {
    // List of all competitors being scored.
    private ArrayList<Competitor> competitors;

    // Contexts that the score is being calculated in.
    private ArrayList<Context> contexts;

    // List of all worldview votes.
    private ArrayList<WorldviewVote> worldviewVotes;
}
