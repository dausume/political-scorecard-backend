package com.asc.politicalscorecard.objects.scoring.dataseries;

import java.util.ArrayList;

public class DataSeries {
    // We define an Id for the data series, for the Dimensions to hook onto.
    private String id;

    // We define a name for the data series.
    private String name;

    // We expect at least one dimension to be present in the data series.
    // We define a set of dimensions via their ids that are expected to be present in the data series.
    private ArrayList<String> dimensionIds;
}