package com.asc.politicalscorecard.objects.scoring.competitors;

// A competitor in the scoring system.
// May be a person, organization, or other entity.
public class Competitor {
    // The id of the competitor.
    private String id;

    // The name of the competitor.
    private String name;

    // The type of the competitor.
    private String type;

    // The id of the category the competitor belongs to.
    private String categoryId;

    // The id of the competitor's parent, if applicable.
    private String parentId;

    // The id of the competitor's parent category, if applicable.
    private String parentCategoryId;
}