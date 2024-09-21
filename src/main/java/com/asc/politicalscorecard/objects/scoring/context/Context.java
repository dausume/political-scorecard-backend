package com.asc.politicalscorecard.objects.scoring.context;

public class Context {
    // A term is a 'mathematical' term, in the context of a score, it is specifically a value between 0 and 1 that is used to
    // as a part of a set of terms in a single context to calculate scores.

    // This is the abstract form of the Term, which is used to compare different terms against each other.
    
    // Id of the term.
    private String id;

    // Name of the term.
    private String name;

    // Logical Equivalence Contexts to this context.
    // These are contexts which, despite being described differently or performed in a different order,
    // have the same logical, purpose, and moral reason for existing.  They are interchangeable, and can
    // be objectively compared to one another to find the ideal solution.
    private String[] logicalEquivalenceContexts;

    // Purpose Equivalence or Competitive Contexts to this context.

    // These are contexts which, despite having similar reason for being, are not logically equivalent.
    //
    // Although they may be used for the same purpose, they are not interchangeable, one may be more suitable
    // in a particular situation than the other.  Or some may just be fundamentally better or worse.
    //
    // In some cases these may be contexts that politically oppose one another.
    // in such cases there may be fundamnetally moral or ethical reasons why one context is preferred over another,
    // and in such cases preferences likely will vary per group or individual.
    private String[] competitiveContexts;
}