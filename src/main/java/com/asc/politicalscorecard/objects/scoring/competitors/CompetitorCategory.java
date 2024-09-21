package com.asc.politicalscorecard.objects.scoring.competitors;

// A category of competitors.
public class CompetitorCategory {
    // The id of the category.
    private String id;

    // The name of the category.
    private String name;

    // Defines whether the category is a root category, the highest level ancestor of categories.
    private boolean isRoot;

    // The id of the parent category, if applicable.
    private String parentId;

    // The id of the category's highest level ancestor, if applicable.
    private String ancestorId;
}