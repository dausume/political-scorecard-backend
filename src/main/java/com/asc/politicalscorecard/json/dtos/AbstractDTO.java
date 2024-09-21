package com.asc.politicalscorecard.json.dtos;

import java.io.Serializable;
import java.util.UUID;

public abstract class AbstractDTO implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Common fields for all DTOs can be defined here
    private String id;

    // Getters and setters for common fields
    public String getId() {
        return id;
    }

    // Used when we are 'setting' an id for something that came in either from the Database or from json, transforming it into Java format.
    public void setId(String id) {
        this.id = id;
    }

    // Generates a random universally unique id and sets that to be the id.
    public void setId() {
        this.id = UUID.randomUUID().toString();
    }

    // Abstract method to be implemented by specific DTOs to convert to entity
    public abstract <T> T toEntity();

    // Abstract method to be implemented by specific DTOs
    public abstract String toString();
}
