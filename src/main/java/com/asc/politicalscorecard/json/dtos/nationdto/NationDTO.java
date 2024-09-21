package com.asc.politicalscorecard.json.dtos.nationdto;

import com.asc.politicalscorecard.json.deserializer.nationdeserializers.NationDeserializer;
import com.asc.politicalscorecard.json.dtos.AbstractDTO;
import com.asc.politicalscorecard.json.dtos.planetdto.PlanetDTO;
import com.asc.politicalscorecard.json.serializer.nationserializers.NationSerializer;
import com.asc.politicalscorecard.objects.location.Nation;
import com.asc.politicalscorecard.objects.location.Planet;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = NationSerializer.class)
@JsonDeserialize(using = NationDeserializer.class)
public class NationDTO extends AbstractDTO {
    private String nationName;
    private String homePlanetId;

    public NationDTO() {
        // No-argument constructor
    }

    // For existing Nation Objects.
    public NationDTO(String id, String nationName, String homePlanetId) {
        super.setId(id);
        this.nationName = nationName;
        this.homePlanetId = homePlanetId;
    }

    // For new Nation objects.
    public NationDTO(String nationName, String homePlanetId) {
        super.setId();  // Automatically generates a new UUID
        this.nationName = nationName;
        this.homePlanetId = homePlanetId;
    }

    // For new Nation objects.
    public NationDTO(String nationName) {
        super.setId();  // Automatically generates a new UUID
        this.nationName = nationName;
    }
    

    // Getter and Setter for nationName
    public String getNationName() {
        return nationName;
    }

    public void setNationName(String nationName) {
        this.nationName = nationName;
    }

    @Override
    public Nation toEntity() {
        if (this.getId() == null || this.getId().isEmpty()) {
            // Case when the DTO is for a new Nation to be saved
            return new Nation(getId(), getNationName(), getHomePlanetId());
        } else {
            // Case when the DTO represents an existing Nation
            return new Nation(getId(), getNationName(), getHomePlanetId());
        }
    }

    public String getHomePlanetId() {
        return homePlanetId;
    }

    public void setHomePlanetId(String homePlanetId) {
        this.homePlanetId = homePlanetId;
    }

    @Override
    public String toString() {
        return "NationDTO [id=" + getId() + ", nationName=" + nationName + ", homePlanet=" + getHomePlanetId() + "]";
    }
}
