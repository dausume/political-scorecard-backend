package com.asc.politicalscorecard.objects.scoring.terms;

// A term is a 'mathematical' term, in the context of a score, it is specifically a value between 0 and 1 that is used to
// as a part of a set of terms in a single context to calculate scores.

// A CompetitiveTermDimension is a series of terms in a single dimension where each each term within it
// is a value between 0 and 1 which correspends to a specific competitor and their score.

// This specifies the competitiveDimension, which is used to understand how the values are derived.
// The context-specific version of a term is the contextTerm object.
// The value of a term specific to a particular score in a context is the contextTermValue object.

// A term can also be considered a 'dimension' but it is not the same as a dimension in a data series.
// A term is a dimension in the context of a score, and whereas a dimension in a data series may be anything
// a term is
public class TermDimension {

    // The data series and their dimensions that would be expected when deriving a value for this term.
    private String[] dataSeriesIds;

    // The url and it's parameters that should be passed to it on Polari, where we can pass
    // the data series and their dimensions to, in order to get back the 
    private String polariUrl;

    // The name of the term dimension.
    private String name;

    // The processed term values for this term dimension.
    private TermValue[] termValues;
}