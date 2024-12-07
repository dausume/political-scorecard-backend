package com.asc.politicalscorecard.objects.policy.scientificcontext.dataseries;

// A single dimension of a data series.
// A dimension is a single aspect of the data series that we are interested in.
public class DataSeriesDimension {
    // The name of the dimension.
    private String name;

    // The type of data we expect to see in this dimension.
    private String dataType;

    // The origin of the data we expect to see in this dimension.
    private String dataOriginId;
}