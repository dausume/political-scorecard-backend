package com.asc.politicalscorecard.json.mixins;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.boot.jackson.JsonMixin;

@JsonMixin
public abstract class PlanetMixin {
    @JsonCreator
    public PlanetMixin(@JsonProperty("id") String id, @JsonProperty("planetName") String planetName) {
    }
}