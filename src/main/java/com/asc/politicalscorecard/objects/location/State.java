package com.asc.politicalscorecard.objects.location;

public class State {
    private String id;

    private String stateName;

    private String parentNationId;

    public State(){}

    public State(String id, String stateName) {
        this.id = id;
        this.stateName = stateName;
    }

     // Note : To create a Sate using a Nation Id, overload the createState with a string in the parameter location of parentNation.
     public State(String id, String stateName, String parentNationId) {
        this.id = id;
        this.stateName = stateName;
        this.parentNationId = parentNationId;
    }

    public String getId() {
        return id;
    }

    public String getStateName() {
        return stateName;
    }

    public String getParentNationId() {
        return parentNationId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public void setParentNationId(String parentNationId) {
        this.parentNationId = parentNationId;
    }

}