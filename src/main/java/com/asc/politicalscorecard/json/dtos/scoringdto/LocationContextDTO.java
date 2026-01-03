package com.asc.politicalscorecard.json.dtos.scoringdto;

import com.asc.politicalscorecard.objects.scoring.terms.context.LocationContext;
import com.asc.politicalscorecard.objects.scoring.terms.context.TermContextType;

/**
 * DTO for Location Context.
 * Supports hierarchical locations: city -> state -> country -> region.
 * Specificity increases from region (least specific) to city (most specific).
 */
public class LocationContextDTO extends TermContextDTO {

    private String country;
    private String state;
    private String city;
    private String region;

    // Constructors
    public LocationContextDTO() {
        super(TermContextType.LOCATION, "");
    }

    public LocationContextDTO(String label, String country, String state, String city, String region) {
        super(TermContextType.LOCATION, label);
        this.country = country;
        this.state = state;
        this.city = city;
        this.region = region;
    }

    // Convenience constructors for different specificity levels

    public static LocationContextDTO forRegion(String region) {
        LocationContextDTO dto = new LocationContextDTO();
        dto.setRegion(region);
        dto.setLabel(region);
        return dto;
    }

    public static LocationContextDTO forCountry(String country) {
        LocationContextDTO dto = new LocationContextDTO();
        dto.setCountry(country);
        dto.setLabel(country);
        return dto;
    }

    public static LocationContextDTO forState(String state, String country) {
        LocationContextDTO dto = new LocationContextDTO();
        dto.setState(state);
        dto.setCountry(country);
        dto.setLabel(state + ", " + country);
        return dto;
    }

    public static LocationContextDTO forCity(String city, String state, String country) {
        LocationContextDTO dto = new LocationContextDTO();
        dto.setCity(city);
        dto.setState(state);
        dto.setCountry(country);
        dto.setLabel(city + ", " + state + ", " + country);
        return dto;
    }

    // Getters and Setters
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * Get the specificity level of this location.
     * Higher number = more specific.
     */
    public int getSpecificityLevel() {
        if (city != null) return 4;
        if (state != null) return 3;
        if (country != null) return 2;
        if (region != null) return 1;
        return 0;
    }

    /**
     * Check if this location is more specific than another.
     */
    public boolean isMoreSpecificThan(LocationContextDTO other) {
        return this.getSpecificityLevel() > other.getSpecificityLevel();
    }

    @Override
    public String getValue() {
        StringBuilder sb = new StringBuilder();
        if (city != null) sb.append(city);
        if (state != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(state);
        }
        if (country != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(country);
        }
        if (region != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(region);
        }
        return sb.toString();
    }

    @Override
    public LocationContext toEntity() {
        LocationContext context = new LocationContext();
        context.setId(this.getId());
        context.setLabel(this.getLabel());
        context.setCountry(this.country);
        context.setState(this.state);
        context.setCity(this.city);
        context.setRegion(this.region);
        return context;
    }

    public static LocationContextDTO fromEntity(LocationContext context) {
        if (context == null) {
            return null;
        }
        LocationContextDTO dto = new LocationContextDTO();
        dto.setId(context.getId());
        dto.setLabel(context.getLabel());
        dto.setCountry(context.getCountry());
        dto.setState(context.getState());
        dto.setCity(context.getCity());
        dto.setRegion(context.getRegion());
        return dto;
    }

    @Override
    public String toString() {
        return "LocationContextDTO{" +
                "id='" + getId() + '\'' +
                ", label='" + getLabel() + '\'' +
                ", country='" + country + '\'' +
                ", state='" + state + '\'' +
                ", city='" + city + '\'' +
                ", region='" + region + '\'' +
                ", specificityLevel=" + getSpecificityLevel() +
                '}';
    }
}
