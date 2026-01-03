package com.asc.politicalscorecard.objects.scoring.terms.context;

import java.util.ArrayList;
import java.util.List;

public class LocationContext extends TermContext {
    private String country;
    private String state;
    private String city;
    private String region;

    public LocationContext() {
        super(TermContextType.LOCATION, "");
    }

    public LocationContext(String label, String country, String state, String city, String region) {
        super(TermContextType.LOCATION, label);
        this.country = country;
        this.state = state;
        this.city = city;
        this.region = region;
    }

    @Override
    public String getValue() {
        List<String> parts = new ArrayList<>();
        if (city != null && !city.isEmpty()) parts.add(city);
        if (state != null && !state.isEmpty()) parts.add(state);
        if (country != null && !country.isEmpty()) parts.add(country);
        if (region != null && !region.isEmpty()) parts.add(region);
        return String.join(", ", parts);
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

    @Override
    public String toString() {
        return "LocationContext{" +
                "label='" + label + '\'' +
                ", value='" + getValue() + '\'' +
                '}';
    }
}
