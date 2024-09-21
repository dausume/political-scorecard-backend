package com.asc.politicalscorecard.objects.scoring.politicalcategory;

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
