package com.asc.politicalscorecard.databases;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.DependsOn; 
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.asc.politicalscorecard.databases.datasourceinitializers.scoringdatasource.ScoringDatasourceInitializer;
import com.asc.politicalscorecard.databases.datasourceproperties.MariaDatasourceProperties;
import com.asc.politicalscorecard.databases.datasourceproperties.RedisDatasourceProperties;
import com.asc.politicalscorecard.databases.datasourceinitializers.geolocationdatasource.GeoLocationDatasourceInitializer;
import com.asc.politicalscorecard.databases.datasourceinitializers.locationsdatasource.LocationDatasourceInitializer;

import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;

// Consolidates the configuration of all data sources used in the application.
@Configuration(proxyBeanMethods = false)
@Scope("singleton")
public class DataSourceConfiguration {
    private final ApplicationContext applicationContext;
    

    DataSourceConfiguration(ApplicationContext applicationContext) 
    {
        this.applicationContext = applicationContext;
    }

    // PRIMARY DATABASE ====================================================================================================
    // This is the primary database that the application uses.
    // It is used for initializing the other databases.

    @Bean
    @Primary
    @Scope("singleton")
    @ConfigurationProperties("app.datasource.mariadb-primary")
    public MariaDatasourceProperties primaryDataSourceProperties() {
        return new MariaDatasourceProperties();
    }

    @Bean
    @Scope("singleton")
    @DependsOn("primaryDataSourceProperties")
    public DataSource primaryDataSource(@Qualifier("primaryDataSourceProperties") MariaDatasourceProperties primaryDataSourceProperties) {
        return DataSourceBuilder
                .create()
                .url(primaryDataSourceProperties.getUrl())
                .username(primaryDataSourceProperties.getUsername())
                .password(primaryDataSourceProperties.getPassword())
                .driverClassName(primaryDataSourceProperties.getDriverClassName())
                .build();
    }

    @Bean
    @Scope("singleton")
    @DependsOn("primaryDataSource")
    JdbcClient primaryJdbcClient(@Qualifier("primaryDataSource") DataSource dataSource) {
        return JdbcClient.create(dataSource);
    }

    // Redis Primary Database ====================================================================================================
    
    @Bean
    @Scope("singleton")
    @Primary
    @ConfigurationProperties("app.datasource.redis-primary")
    public RedisDatasourceProperties primaryRedisProperties() {
        return new RedisDatasourceProperties();
    }

    @Bean
    @Scope("singleton")
    @Primary
    @DependsOn("primaryRedisProperties")
    public LettuceConnectionFactory primaryRedisConnectionFactory(@Qualifier("primaryRedisProperties") RedisProperties redisProperties) {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisProperties.getHost());
        redisStandaloneConfiguration.setPort(redisProperties.getPort());
        // Set the username if provided
        if (redisProperties.getUsername() != null && !redisProperties.getUsername().isEmpty()) {
            redisStandaloneConfiguration.setUsername(redisProperties.getUsername());
        }
        redisStandaloneConfiguration.setPassword(redisProperties.getPassword());
        // Explicitly initialize the connection factory here to catch any issues
        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisStandaloneConfiguration);
        factory.afterPropertiesSet(); // This forces the factory to initialize immediately
        return factory;
    }

    @Bean
    @Scope("singleton")
    @DependsOn("primaryRedisConnectionFactory") // IMPORTANT!!  The first redis template MUST be named redisTemplate, or everything breaks.
    public RedisTemplate<String, String> redisTemplate(@Qualifier("primaryRedisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
        System.out.println("In redisTemplate creation");
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        System.out.println("After redisTemplate creation");
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        System.out.println("After setConnectionFactory");
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }
    
    // scoring DATABASE ====================================================================================================

    @Bean
    @Scope("singleton")
    @ConfigurationProperties("app.datasource.scoring")
    @DependsOn("primaryJdbcClient") // The context datasource depends on the primary mariadb database existing.
    public MariaDatasourceProperties scoringDataSourceProperties() {
        return new MariaDatasourceProperties();
    }

    @Bean
    @Scope("singleton")
    @DependsOn("scoringDataSourceProperties")
    public DataSource scoringDataSource(@Qualifier("scoringDataSourceProperties") MariaDatasourceProperties scoringDataSourceProperties) {
        System.out.println("Scoring database properties: " + scoringDataSourceProperties);
        System.out.println("Scoring database name: " + scoringDataSourceProperties.getDatabaseName());
        System.out.println("Scoring username: " + scoringDataSourceProperties.getUsername());
        return DataSourceBuilder
                .create()
                .url(scoringDataSourceProperties.getUrl())
                .username(scoringDataSourceProperties.getUsername())
                .password(scoringDataSourceProperties.getPassword())
                .driverClassName(scoringDataSourceProperties.getDriverClassName())
                .build();
    }

	@Bean
    @Scope("singleton")
    @DependsOn({"scoringDataSource", "databaseInitializer"})
	JdbcClient scoringJdbcClient(@Qualifier("scoringDataSource") DataSource dataSource) {
		return JdbcClient.create(dataSource);
	}

    // LOCATION DATABASE ====================================================================================================

    @Bean
    @Scope("singleton")
    @ConfigurationProperties("app.datasource.location")
    @DependsOn("primaryJdbcClient") // The location datasource depends on the primary mariadb database existing.
    public MariaDatasourceProperties locationDataSourceProperties() {
        return new MariaDatasourceProperties();
    }

    @Bean
    @Scope("singleton")
    @DependsOn("locationDataSourceProperties")
    public DataSource locationDataSource(@Qualifier("locationDataSourceProperties") MariaDatasourceProperties locationDataSourceProperties) {
        System.out.println("Location database properties: " + locationDataSourceProperties);
        System.out.println("Location database name: " + locationDataSourceProperties.getDatabaseName());
        System.out.println("Location username: " + locationDataSourceProperties.getUsername());
        return DataSourceBuilder
                .create()
                .url(locationDataSourceProperties.getUrl())
                .username(locationDataSourceProperties.getUsername())
                .password(locationDataSourceProperties.getPassword())
                .driverClassName(locationDataSourceProperties.getDriverClassName())
                .build();
    }

	@Bean
    @Scope("singleton")
    @DependsOn({"locationDataSource", "databaseInitializer"})
	JdbcClient locationJdbcClient(@Qualifier("locationDataSource") DataSource dataSource) {
		return JdbcClient.create(dataSource);
	}

    // GeoLocation Database ====================================================================================================
    
    @Bean
    @Scope("singleton")
    @ConfigurationProperties("app.datasource.geolocation")
    @DependsOn("redisTemplate") // The geoLocation datasource depends on the primary redis database existing.
    public RedisDatasourceProperties geoLocationDataSourceProperties() {
        return new RedisDatasourceProperties();
    }

    
    @Bean
    @Scope("singleton")
    @DependsOn("geoLocationDataSourceProperties")
    public LettuceConnectionFactory geoLocationDataSource(@Qualifier("geoLocationDataSourceProperties") RedisDatasourceProperties redisProperties) {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisProperties.getHost());
        redisStandaloneConfiguration.setPort(redisProperties.getPort());
        redisStandaloneConfiguration.setDatabase((redisProperties.getDatabaseIndex()) );// convert to integer
        // Set the username if provided
        if (redisProperties.getUsername() != null && !redisProperties.getUsername().isEmpty()) {
            redisStandaloneConfiguration.setUsername(redisProperties.getUsername());
        }
        redisStandaloneConfiguration.setPassword(redisProperties.getPassword());
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }
        
    
    @Bean
    @Scope("singleton")
    @DependsOn({"geoLocationDataSource", "databaseInitializer"})
    public RedisTemplate<String, String> geoLocationRedisClient(@Qualifier("geoLocationDataSource") RedisConnectionFactory redisConnectionFactory) {
        System.out.println("In geoLocationDataSource creation");
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }
    

    // DATABASE INITIALIZATION ============================================================================================

    public class DatabaseInitializer 
    {
        // Let us define a boolean value indicating whether the initialization was successful.
        @Autowired
        private InitializationState initializationState;

        private ScoringDatasourceInitializer scoringDatasourceInitializer = null;

        private LocationDatasourceInitializer locationDatasourceInitializer = null;

        private GeoLocationDatasourceInitializer geoLocationDatasourceInitializer = null;

        @Bean
        @Scope("singleton")
        @DependsOn({"scoringDataSourceProperties", "locationDataSourceProperties", "geoLocationDataSourceProperties"})
        public DatabaseInitializer databaseInitializationSettings(
            @Qualifier("scoringDataSourceProperties") MariaDatasourceProperties scoringDataSourceProperties,
            @Qualifier("locationDataSourceProperties") MariaDatasourceProperties locationDataSourceProperties,
            @Qualifier("geoLocationDataSourceProperties") RedisDatasourceProperties geoLocationDataSourceProperties
            ) 
        {
            System.out.println("Setting database initialization settings...");
            System.out.println("GeoLocation database props and name: " + geoLocationDataSourceProperties + " : " + geoLocationDataSourceProperties.getNamespace());
            System.out.println("Location database props and name: " + locationDataSourceProperties + " : " + locationDataSourceProperties.getDatabaseName());
            // Set the database names
            initializationState.setScoringDatabaseName(scoringDataSourceProperties.getDatabaseName());
            initializationState.setLocationDatabaseName(locationDataSourceProperties.getDatabaseName());
            initializationState.setGeoLocationDatabaseName(geoLocationDataSourceProperties.getNamespace());
            return this;
        }

        // Call initializers for all data sources using DatabaseInitializer class.
        // jdbcClients do not need to be passed because they are defined via @Qualifier in the DatabaseInitializer class.
        @Bean
        @Scope("singleton")
        @DependsOn({"primaryJdbcClient", "redisTemplate", "databaseInitializationSettings"})
        public DatabaseInitializer databaseInitializer(
            @Qualifier("primaryJdbcClient") JdbcClient primaryJdbcClient,
            @Qualifier("redisTemplate") RedisTemplate<String, String> primaryRedisTemplate
            ) 
        {
            System.out.println("Starting database initialization...");
            try {
                // I need these initializers to run and then detect if they completed successfully
                // if all completed successfully, then I can start the application.
                
                // Initialize context database
                scoringDatasourceInitializer = new ScoringDatasourceInitializer(
                    primaryJdbcClient,
                    applicationContext,
                    initializationState
                );

                // Initialize location database
                locationDatasourceInitializer = new LocationDatasourceInitializer(
                    primaryJdbcClient,
                    applicationContext,
                    initializationState
                    );
                
                geoLocationDatasourceInitializer = new GeoLocationDatasourceInitializer(
                    primaryRedisTemplate,
                    applicationContext,
                    initializationState
                );

                scoringDatasourceInitializer.createDatabaseIfNotExists();
                locationDatasourceInitializer.createDatabaseIfNotExists();
                geoLocationDatasourceInitializer.createNamespaceIfNotExists();
                System.out.println("All initializers started, awaiting database initialization completion.");
                // Wait for all databases to be ready
                initializationState.waitForDatabaseInitializations();
                System.out.println("All databases initialized successfully.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Database initialization interrupted: " + e.getMessage());
            }

            return this;
        }

        @Bean
        @Scope("singleton")
        @DependsOn({"databaseInitializer", "locationJdbcClient", "scoringJdbcClient", "geoLocationRedisClient"})
        public DatabaseInitializer databaseTablesInitializer(
            @Qualifier("databaseInitializer") DatabaseInitializer dbInit
            ) 
        {
            System.out.println("Starting table initialization...");
            try {
                // Wait for all tables to be ready
                if (
                    initializationState.isInitializedScoringDatabase() && 
                    initializationState.isInitializedLocationDatabase() &&
                    initializationState.isInitializedGeoLocationDatabase()
                    ) 
                    {
                    scoringDatasourceInitializer.initialize();
                    locationDatasourceInitializer.initialize();
                    geoLocationDatasourceInitializer.initialize();
                    System.out.println("All initializers completed successfully. Application can start.");
                }
                else
                {
                    System.out.println("Not all initializers completed successfully. Application cannot start.");
                }
                // Check if all initializations were successful
                initializationState.waitForTableInitializations();
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Table initialization interrupted: " + e.getMessage());
            }

            return this;
        }
    }

}