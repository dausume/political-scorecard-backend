package com.asc.politicalscorecard.objects.scoring.terms;

// A context term is a transform that is performed on a term in order to coerce it into a format appropriate
// for a more specific context than what the term is originally intended for.
public class ContextualizedTerm {
    // A context term is a term that is used in the context of a score, it is specifically a value between 0 and 1 that is used to
    // as a part of a set of terms in a single context to calculate scores.

    // This is the abstract form of the Context Term, which is used to compare different terms against each other.
    
    // Id of the context term.
    private String id;

    // Name of the context term.
    private String name;

    // Link to a Pre-Processing Polari URL that can be used to perform additional context-specific transforms on the term.
    // This is used to perform transforms on a data series before it is used to calculate the term values between 0 and 1.
    private String preProcessPolariUrl;

    // Link to a Post-Processing Polari URL that can be used to perform additional context-specific transforms on the term.
    // This is used to perform additional transforms on the term values between 0 and 1, after they have been calculated,
    // but before they are used to calculate the final score.
    private String postProcessPolariUrl;
}
