package com.asc.politicalscorecard.objects.location;

public class County {
    private String id;

    private String countyName;

    private String parentStateId;

    public County(){}

    // Constructor
    public County(String id, String countyName, String parentStateId) {
        this.id = id;
        this.countyName = countyName;
        this.parentStateId = parentStateId;
    }


    public County(String id, String countyName) {
        this.id = id;
        this.countyName = countyName;
    }

    // Getter
    public String getId() {
        return id;
    }

    // Getter
    public String getCountyName() {
        return countyName;
    }

    // Getter
    public String getParentStateId() {
        return parentStateId;
    }

    // Setter
    public void setId(String id) {
        this.id = id;
    }

    // Setter
    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    // Setter
    public void setParentStateId(String parentStateId) {
        this.parentStateId = parentStateId;
    }
}