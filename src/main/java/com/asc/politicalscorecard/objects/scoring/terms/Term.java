package com.asc.politicalscorecard.objects.scoring.terms;

public class Term {
    // A term is a 'mathematical' term, in the context of a score, it is specifically a value between 0 and 1 that is used to
    // as a part of a set of terms in a single context to calculate scores.

    // This is the abstract form of the Term, which is used to compare different terms against each other.
    
    // Id of the term.
    private String id;

    // Name of the term.
    private String name;

    // Logical Equivalence Terms to this context.
    private String[] logicalEquivalenceTerms;

    // Purpose Equivalence or Competitive Terms to this context.
    private String[] competitiveTerms;
}