package com.asc.politicalscorecard.objects.scoring.context;

// Represents a reference frame for a context, used to narrow down the scope of data for a context.
public class ReferenceFrame {
    // The id of the reference frame.
    private String id;

    // The name of the reference frame.
    private String name;

    // The list of reference frame segments that make up the reference frame.
    private ReferenceFrameSegment[] referenceFrameSegments;
}