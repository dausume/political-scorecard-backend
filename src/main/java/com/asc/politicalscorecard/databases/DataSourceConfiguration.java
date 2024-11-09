package com.asc.politicalscorecard.databases;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.init.DataSourceScriptDatabaseInitializer;
import org.springframework.boot.sql.init.DatabaseInitializationMode;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
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
//import autowired
import org.springframework.beans.factory.annotation.Autowired;

import com.asc.politicalscorecard.databases.datasourceinitializers.scoringdatasource.ScoringDatasourceInitializer;
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
        System.out.println("Starting DataSourceConfiguration");
        this.applicationContext = applicationContext;
    }

    // PRIMARY DATABASE ====================================================================================================
    // This is the primary database that the application uses.
    // It is used for initializing the other databases.

    @Bean
    @Primary
    @Scope("singleton")
    @ConfigurationProperties("app.datasource.mariadb-primary")
    public DataSourceProperties primaryDataSourceProperties() {
        System.out.println("Creating PrimaryDataSourceProperties");
        return new DataSourceProperties();
    }

    @Bean
    @Scope("singleton")
    @DependsOn("primaryDataSourceProperties")
    public DataSource primaryDataSource(@Qualifier("primaryDataSourceProperties") DataSourceProperties primaryDataSourceProperties) {
        
        
        System.out.println("In primaryDataSource creation with properties: " 
        + " Url: " + primaryDataSourceProperties.getUrl()
        + " Username: " + primaryDataSourceProperties.getUsername()
        + " Password: " +primaryDataSourceProperties.getPassword()
        + " DriverClassName: " + primaryDataSourceProperties.getDriverClassName()
        );
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
        System.out.println("In primaryJdbcClient creation");
        return JdbcClient.create(dataSource);
    }

    // Redis Primary Database ====================================================================================================
    
    @Bean
    @Scope("singleton")
    @Primary
    @ConfigurationProperties("app.datasource.redis-primary")
    public RedisProperties primaryRedisProperties() {
        return new RedisProperties();
    }

    @Bean
    @Scope("singleton")
    @Primary
    @DependsOn("primaryRedisProperties")
    public LettuceConnectionFactory primaryRedisConnectionFactory(@Qualifier("primaryRedisProperties") RedisProperties redisProperties) {
        System.out.println("In redisConnectionFactory creation with properties: " 
        + " Host: " + redisProperties.getHost()
        + " Port: " + redisProperties.getPort()
        + " Username: " + redisProperties.getUsername()
        + " Password: " + redisProperties.getPassword()
        );
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
        return redisTemplate;
    }
    
    // CONTEXT DATABASE ====================================================================================================

    @Bean
    @Scope("singleton")
    @ConfigurationProperties("app.datasource.context")
    @DependsOn("primaryJdbcClient") // The context datasource depends on the primary mariadb database existing.
    public DataSourceProperties contextDataSourceProperties() {
        System.out.println("Creating ContextDataSourceProperties");
        return new DataSourceProperties();

    }

    @Bean
    @Scope("singleton")
    @DependsOn("contextDataSourceProperties")
    public DataSource contextDataSource(@Qualifier("contextDataSourceProperties") DataSourceProperties contextDataSourceProperties) {
        System.out.println("In contextDataSourceProperties creation with properties: " 
        + " Url: " + contextDataSourceProperties.getUrl()
        + " Username: " + contextDataSourceProperties.getUsername()
        + " Password: " + contextDataSourceProperties.getPassword()
        + " DriverClassName: " + contextDataSourceProperties.getDriverClassName()
        );
        return DataSourceBuilder
                .create()
                .url(contextDataSourceProperties.getUrl())
                .username(contextDataSourceProperties.getUsername())
                .password(contextDataSourceProperties.getPassword())
                .driverClassName(contextDataSourceProperties.getDriverClassName())
                .build();
    }

	@Bean
    @Scope("singleton")
    @DependsOn("contextDataSource")
	JdbcClient contextJdbcClient(@Qualifier("contextDataSource") DataSource dataSource) {
        System.out.println("In contextJdbcClient creation");
		return JdbcClient.create(dataSource);
	}

    // LOCATION DATABASE ====================================================================================================

    @Bean
    @Scope("singleton")
    @ConfigurationProperties("app.datasource.location")
    @DependsOn("primaryJdbcClient") // The location datasource depends on the primary mariadb database existing.
    public DataSourceProperties locationDataSourceProperties() {
        System.out.println("Creating LocationDataSourceProperties");
        return new DataSourceProperties();
    }

    @Bean
    @Scope("singleton")
    @DependsOn("locationDataSourceProperties")
    public DataSource locationDataSource(@Qualifier("locationDataSourceProperties") DataSourceProperties locationDataSourceProperties) {
        System.out.println("In locationDataSourceProperties creation with properties: " 
        + " Url: " + locationDataSourceProperties.getUrl()
        + " Username: " + locationDataSourceProperties.getUsername()
        + " Password: " + locationDataSourceProperties.getPassword()
        + " DriverClassName: " + locationDataSourceProperties.getDriverClassName()
        );
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
    @DependsOn("locationDataSource")
	JdbcClient locationJdbcClient(@Qualifier("locationDataSource") DataSource dataSource) {
        System.out.println("In locationJdbcClient creation");
		return JdbcClient.create(dataSource);
	}

    // GeoLocation Database ====================================================================================================
    
    @Bean
    @Scope("singleton")
    @ConfigurationProperties("app.datasource.geolocation")
    @DependsOn("redisTemplate") // The geoLocation datasource depends on the primary redis database existing.
    public RedisProperties geoLocationDataSourceProperties() {
        System.out.println("Creating GeoLocationDataSourceProperties");
        return new RedisProperties();
    }

    
    @Bean
    @Scope("singleton")
    @DependsOn("geoLocationDataSourceProperties")
    public LettuceConnectionFactory geoLocationDataSource(@Qualifier("geoLocationDataSourceProperties") RedisProperties redisProperties) {
        System.out.println("In redisConnectionFactory creation with properties: " 
        + " Host: " + redisProperties.getHost()
        + " Port: " + redisProperties.getPort()
        + " Username: " + redisProperties.getUsername()
        + " Password: " + redisProperties.getPassword()
        );
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisProperties.getHost());
        redisStandaloneConfiguration.setPort(redisProperties.getPort());
        // Set the username if provided
        if (redisProperties.getUsername() != null && !redisProperties.getUsername().isEmpty()) {
            redisStandaloneConfiguration.setUsername(redisProperties.getUsername());
        }
        redisStandaloneConfiguration.setPassword(redisProperties.getPassword());
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }
        
    
    @Bean
    @Scope("singleton")
    @DependsOn("geoLocationDataSource")
    public RedisTemplate<String, String> geoLocationRedisClient(@Qualifier("geoLocationDataSource") RedisConnectionFactory redisConnectionFactory) {
        System.out.println("In geoLocationDataSource creation");
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
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

        // Call initializers for all data sources using DatabaseInitializer class.
        // jdbcClients do not need to be passed because they are defined via @Qualifier in the DatabaseInitializer class.
        @Bean
        @Scope("singleton")
        @DependsOn({"primaryJdbcClient"})
        public DatabaseInitializer databaseInitializer(
            @Qualifier("primaryJdbcClient") JdbcClient primaryJdbcClient,
            @Qualifier("redisTemplate") RedisTemplate<String, String> primaryRedisTemplate
            ) 
        {
            DatabaseInitializer databaseInitializer = new DatabaseInitializer();
            System.out.println("In databaseInitializer");
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
            
            return databaseInitializer;
        }

        @Bean
        @Scope("singleton")
        @DependsOn({"databaseInitializer", "locationJdbcClient", "contextJdbcClient", "geoLocationRedisClient"})
        public DatabaseInitializer databaseTablesInitializer(
            @Qualifier("databaseInitializer") DatabaseInitializer dbInit
            ) 
        {
            System.out.println("Hit DatabaseTablesInitializer");
            System.out.println("InitializationState: " + initializationState);
            // Check if all initializations were successful
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
            return dbInit;
        }
    }
}