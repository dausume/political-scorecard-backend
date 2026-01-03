package com.asc.politicalscorecard.databases.datasourceinitializers.scoringcachedatasource;

import org.springframework.data.redis.core.RedisTemplate;

/**
 * Initializes the TermContext cache in Redis.
 * Stores full TermContext objects with all polymorphic data (TimeframeContext, LocationContext, etc.)
 * that would be complex to store in relational tables.
 */
public class TermContextCacheInitializer {

    private final RedisTemplate<String, String> redisTemplate;
    private final String namespace;

    public TermContextCacheInitializer(RedisTemplate<String, String> redisTemplate, String namespace) {
        this.redisTemplate = redisTemplate;
        this.namespace = namespace;
    }

    public void initializeTable() {
        System.out.println("Initializing TermContext cache in Redis.");
        String termContextKey = namespace + ":term_contexts";

        // Check if the hash already exists
        Boolean exists = redisTemplate.hasKey(termContextKey);
        if (Boolean.FALSE.equals(exists)) {
            // Initialize the hash structure
            redisTemplate.opsForHash().put(termContextKey, "initialized", "true");
            System.out.println("TermContext cache structure created.");
        } else {
            System.out.println("TermContext cache structure already exists.");
        }

        // Note: Actual TermContext objects will be stored as:
        // Key: scoringCache:term_contexts
        // Hash Field: context:{contextId}
        // Hash Value: JSON serialized TermContext object with all polymorphic fields

        System.out.println("TermContext cache initialized. Objects will be stored as:");
        System.out.println("  Key pattern: " + termContextKey);
        System.out.println("  Hash field pattern: context:{contextId}");
        System.out.println("  Value: Full TermContext JSON with polymorphic data");
    }
}
