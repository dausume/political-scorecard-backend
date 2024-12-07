package com.asc.politicalscorecard.objects.scoring.politicalcategory;

// The class that represents a political category.
// This is simply meant to be a means to categorize topics and issues according to conventional political categories.
// Such that the user can filter and search for topics and issues, scores or legislation, based on these categories.
public class PoliticalCategory {
    // The id of the category.
    private String id;

    // The name of the category.
    private String name;

    // The description of the context category.
    private String description;

    // Defines whether the category is a root category, the highest level ancestor of categories.
    private boolean isRoot;

    // The id of the parent category.
    private String[] parentCategories;
}