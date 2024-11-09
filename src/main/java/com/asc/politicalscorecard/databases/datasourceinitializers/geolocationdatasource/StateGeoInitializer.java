package com.asc.politicalscorecard.databases.datasourceinitializers.geolocationdatasource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class StateGeoInitializer {

    private final RedisTemplate<String, String> redisTemplate;
    private final String namespace;

    List<String> stateAbbreviations = Arrays.asList(
    "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA",
    "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD",
    "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ",
    "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC",
    "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"
    );


    public StateGeoInitializer(RedisTemplate<String, String> redisTemplate, String namespace) {
        this.redisTemplate = redisTemplate;
        this.namespace = namespace;
    }

    public void initializeTable() {
        System.out.println("Initializing State Geo table in Redis.");
        String stateKey = namespace + ":states";
        String baseUrl = "https://raw.githubusercontent.com/johan/world.geo.json/master/countries/USA";
        RestTemplate restTemplate = new RestTemplate();

        // Populate the state geo data structure (example data)
        // Can we automate this process to just go via a list of abbreviations, put in the hashKey "state:XX" and then postfix the url /XX.geo.json?
        /* 
        redisTemplate.opsForHash().put(stateKey, "state:CA", 
        restTemplate.getForObject(
            "https://raw.githubusercontent.com/johan/world.geo.json/master/countries/USA/CA.geo.json", 
            String.class
        ));

        */
        // Iterate over each state abbreviation, construct the URL, fetch data, and store it in Redis
        for (String abbreviation : stateAbbreviations) {
            String url = baseUrl + "/" + abbreviation + ".geo.json";
            try {
                if(redisTemplate.opsForHash().hasKey(stateKey, "state:" + abbreviation)) {
                    System.out.println("GeoJSON data for state: " + abbreviation + " already exists. Skipping fetch.");
                    continue;
                }
                else
                {
                    // Fetch GeoJSON data for the state
                    String geoJsonData = restTemplate.getForObject(url, String.class);

                    // Store the fetched GeoJSON data in Redis
                    if (geoJsonData != null) {
                        redisTemplate.opsForHash().put(stateKey, "state:" + abbreviation, geoJsonData);
                        System.out.println("Cached GeoJSON for state: " + abbreviation);
                    } else {
                        System.err.println("No data found for state: " + abbreviation);
                    }
                }
            } catch (Exception e) {
                System.err.println("Failed to fetch data for state: " + abbreviation + " - " + e.getMessage());
            }
        }

        System.out.println("State Geo table initialized with example data.");
    }
}