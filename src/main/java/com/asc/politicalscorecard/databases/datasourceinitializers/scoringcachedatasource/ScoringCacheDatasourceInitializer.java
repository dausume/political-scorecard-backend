package com.asc.politicalscorecard.databases.datasourceinitializers.scoringcachedatasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;

import com.asc.politicalscorecard.databases.InitializationState;

import jakarta.annotation.PostConstruct;

/**
 * Initializes the scoring cache datasource in Redis.
 * Stores complex scoring data structures that don't fit well in relational tables.
 */
@Component
@Scope("singleton")
public class ScoringCacheDatasourceInitializer {

    private final ApplicationContext applicationContext;
    private final RedisTemplate<String, String> primaryRedisTemplate;
    private final InitializationState initializationState;
    private String scoringCacheNamespace = "";

    public ScoringCacheDatasourceInitializer(
        @Qualifier("redisTemplate") RedisTemplate<String, String> primaryRedisTemplate,
        ApplicationContext applicationContext,
        InitializationState initializationState
    ) {
        this.primaryRedisTemplate = primaryRedisTemplate;
        this.applicationContext = applicationContext;
        this.initializationState = initializationState;
        this.scoringCacheNamespace = this.initializationState.getScoringCacheDatabaseName();
    }


    public void createNamespaceIfNotExists() {
        System.out.println("In createNamespaceIfNotExists for ScoringCacheDatasourceInitializer.");
        try {
            // Check if the namespace exists in Redis
            Boolean exists = primaryRedisTemplate.hasKey(scoringCacheNamespace);
            System.out.println("Checking if namespace exists for ScoringCache database: " + scoringCacheNamespace + " - " + exists);
            if (Boolean.FALSE.equals(exists)) {
                System.out.println("Creating namespace for ScoringCache database: " + scoringCacheNamespace);
                // Create the scoringCache namespace in Redis
                // This is a hash key so we can store more hashes within it
                primaryRedisTemplate.opsForHash().put(scoringCacheNamespace, "initialized", "true");
                System.out.println("ScoringCache namespace created.");
                initializationState.setInitializedScoringCacheDatabase(true);
            } else {
                System.out.println("ScoringCache namespace already exists.");
                initializationState.setInitializedScoringCacheDatabase(true);
            }
        } catch (Exception e) {
            System.out.println("Error creating ScoringCache namespace: " + e.getMessage());
        }
    }

    public void initialize() {
        System.out.println("Initializing tables for ScoringCacheDatasourceInitializer.");

        // Get the scoring cache Redis template
        RedisTemplate<String, String> scoringCacheRedisTemplate =
            applicationContext.getBean("scoringCacheRedisClient", RedisTemplate.class);

        // Initialize the "tables" (Redis data structures) for complex scoring data
        TermContextCacheInitializer termContextCacheInitializer =
            new TermContextCacheInitializer(scoringCacheRedisTemplate, scoringCacheNamespace);
        termContextCacheInitializer.initializeTable();

        initializationState.setInitializedScoringCacheTables(true);
    }
}
