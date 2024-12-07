package com.asc.politicalscorecard.databases.datasourceinitializers.geolocationdatasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;

import com.asc.politicalscorecard.databases.InitializationState;

import jakarta.annotation.PostConstruct;

@Component
@Scope("singleton")
public class GeoLocationDatasourceInitializer {

    private final ApplicationContext applicationContext;
    private final RedisTemplate<String, String> primaryRedisTemplate;
    private final InitializationState initializationState;
    private String geoLocationNamespace = "";

    public GeoLocationDatasourceInitializer(
        @Qualifier("redisTemplate") RedisTemplate<String, String> primaryRedisTemplate,
        ApplicationContext applicationContext,
        InitializationState initializationState
    ) {
        this.primaryRedisTemplate = primaryRedisTemplate;
        this.applicationContext = applicationContext;
        this.initializationState = initializationState;
        this.geoLocationNamespace = this.initializationState.getGeoLocationDatabaseName();
    }

    
    public void createNamespaceIfNotExists() {
        System.out.println("In createNamespaceIfNotExists for GeoLocationDatasourceInitializer.");
        try {
            // Check if the namespace exists in Redis
            Boolean exists = primaryRedisTemplate.hasKey(geoLocationNamespace);
            System.out.println("Checking if namespace exists for GeoLocation database: " + geoLocationNamespace + " - " + exists);
            if (Boolean.FALSE.equals(exists)) {
                System.out.println("Creating namespace for GeoLocation database: " + geoLocationNamespace);
                // This should create the geoLocations namespace in Redis in db0
                // This should be a hash key so that we can store more hashes within it.
                primaryRedisTemplate.opsForHash().put(geoLocationNamespace, "initialized", "true");
                System.out.println("GeoLocation namespace created.");
                initializationState.setInitializedGeoLocationDatabase(true);
            } else {
                System.out.println("GeoLocation namespace already exists.");
                initializationState.setInitializedGeoLocationDatabase(true);
            }
        } catch (Exception e) {
            System.out.println("Error creating GeoLocation namespace: " + e.getMessage());
        }
    }

    public void initialize() {
        System.out.println("Initializing tables for GeoLocationDatasourceInitializer.");

        NationGeoInitializer nationGeoInitializer = new NationGeoInitializer(primaryRedisTemplate, geoLocationNamespace);
        StateGeoInitializer stateGeoInitializer = new StateGeoInitializer(primaryRedisTemplate, geoLocationNamespace);

        // Initialize the "tables" (Redis data structures)
        nationGeoInitializer.initializeTable();
        stateGeoInitializer.initializeTable();

        initializationState.setInitializedGeoLocationTables(true);
    }
}