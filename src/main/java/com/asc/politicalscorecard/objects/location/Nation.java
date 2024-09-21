package com.asc.politicalscorecard.objects.location;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.asc.politicalscorecard.services.PlanetService;

public class Nation {

    private String id;

    private String nationName;

    private String homePlanetId;

    public Nation() {
    }

    // Used for existing Nation objects
    public Nation(String id, String nationName, String homePlanetId) {
        this.id = id;
        this.nationName = nationName;
        this.homePlanetId = homePlanetId; 
    }

    // Used for new Nation objects
    //make a shallow planet with only the id, this should not be used when updating both this
    //and the planet at the same time or it will cause data loss.  Only when quickly using the id to make the connection.
    public Nation(String nationName, String homePlanetId) {
        this.nationName = nationName;
        this.homePlanetId = homePlanetId; 
    }

    // Getter
    public String getId() {
        return id;
    }

    // Getter
    public String getNationName() {
        return nationName;
    }

    // Getter
    public String getHomePlanetId() {
        return homePlanetId;
    }

    // Setter
    public void setId(String id) {
        this.id = id;
    }

    // Setter
    public void setNationName(String nationName) {
        this.nationName = nationName;
    }

    // Setter
    public void setHomePlanetId(String homePlanetId) {
        this.homePlanetId = homePlanetId;
    }
}