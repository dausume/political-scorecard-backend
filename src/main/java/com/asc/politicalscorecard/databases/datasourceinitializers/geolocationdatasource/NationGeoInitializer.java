package com.asc.politicalscorecard.databases.datasourceinitializers.geolocationdatasource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;
//import org.springframework.http.ResponseEntity;

public class NationGeoInitializer {

    private final RedisTemplate<String, String> redisTemplate;
    private final String namespace;

    public NationGeoInitializer(RedisTemplate<String, String> redisTemplate, String namespace) {
        this.redisTemplate = redisTemplate;
        this.namespace = namespace;
    }

    // List of countries and their URLs
    String[][] nations = {
        {"nation:US", "https://raw.githubusercontent.com/johan/world.geo.json/master/countries/USA.geo.json"},
        {"nation:CAN", "https://raw.githubusercontent.com/johan/world.geo.json/master/countries/CAN.geo.json"},
        {"nation:MEX", "https://raw.githubusercontent.com/johan/world.geo.json/master/countries/MEX.geo.json"},
        {"nation:BRA", "https://raw.githubusercontent.com/johan/world.geo.json/master/countries/BRA.geo.json"},
        {"nation:GRL", "https://raw.githubusercontent.com/johan/world.geo.json/master/countries/GRL.geo.json"},
        {"nation:CUB", "https://raw.githubusercontent.com/johan/world.geo.json/master/countries/CUB.geo.json"}
    };

    public void initializeTable() {
        String nationKey = namespace + ":nations";

        RestTemplate restTemplate = new RestTemplate();

        for (String[] nation : nations) {
            String hashKey = nation[0];
            String url = nation[1];
            //System.out.println("Checking existence for key: " + nationKey + ", hashKey: " + hashKey);
            boolean exists = redisTemplate.opsForHash().hasKey(nationKey, hashKey);
            //System.out.println("Key exists: " + exists);
            // Check if the data for this nation already exists in Redis
            if (!exists) {
                try {
                    // Fetch the GeoJSON data from the URL
                    String geoJsonData = restTemplate.getForObject(url, String.class);
                    //System.out.println("Caching the GeoJSON : " + geoJsonData);
                    if (geoJsonData != null) {
                        // Store the GeoJSON data in Redis only if it's not already present
                        redisTemplate.opsForHash().put(nationKey, hashKey, geoJsonData);
                        //System.out.println("Cached GeoJSON for " + hashKey);
                    } else {
                        System.err.println("No data found for " + hashKey);
                    }
                    /*
                    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                        geoJsonData = response.getBody();
                        System.out.println("GeoJSON fetched: " + geoJsonData);
                        redisTemplate.opsForHash().put(nationKey, hashKey, geoJsonData);
                        System.out.println("Cached GeoJSON for " + hashKey);
                    } else {
                        System.err.println("Failed to fetch GeoJSON for " + hashKey + ": HTTP " + response.getStatusCode());
                    }
                        */
                } catch (Exception e) {
                    System.err.println("Failed to fetch data for " + hashKey + ": " + e.getMessage());
                }
            } else {
                // Skip fetching data if it already exists
                // For some reason when we hit this the service is not able to fetch the data
                // Retrieve and log the data to validate
                //Object storedData = redisTemplate.opsForHash().get(nationKey, hashKey);
                //System.out.println("Retrieved data for " + hashKey + ": " + storedData);
                System.out.println("GeoJSON data for " + hashKey + " already exists. Skipping fetch.");
            }
        }
        System.out.println("nation Geo table initialized with example data.");
    }
}