package com.asc.politicalscorecard.objects.scoring.dataseries;

// A single dimension of a data series.
public class Dimension {
    // The name of the dimension.
    private String name;

    // The type of data we expect to see in this dimension.
    private String dataType;

    // The origin of the data we expect to see in this dimension.
    private String dataOriginId;
}