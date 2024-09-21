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

import com.asc.politicalscorecard.databases.datasourceinitializers.contextdatasource.ContextDatasourceInitializer;
import com.asc.politicalscorecard.databases.datasourceinitializers.locationsdatasource.LocationDatasourceInitializer;

import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;

// Consolidates the configuration of all data sources used in the application.
@Configuration(proxyBeanMethods = false)
@Scope("singleton")
public class DataSourceConfiguration {
    private final ApplicationContext applicationContext;
    

    DataSourceConfiguration(ApplicationContext applicationContext) {
        System.out.println("Starting DataSourceConfiguration");
        this.applicationContext = applicationContext;
    }

    // PRIMARY DATABASE ====================================================================================================
    // This is the primary database that the application uses.
    // It is used for initializing the other databases.

    @Bean
    @Primary
    @ConfigurationProperties("app.datasource.primary")
    public DataSourceProperties primaryDataSourceProperties() {
        System.out.println("Creating PrimaryDataSourceProperties");
        return new DataSourceProperties();
    }

    @Bean
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
    @DependsOn("primaryDataSource")
    JdbcClient primaryJdbcClient(@Qualifier("primaryDataSource") DataSource dataSource) {
        System.out.println("In primaryJdbcClient creation");
        return JdbcClient.create(dataSource);
    }

    // CONTEXT DATABASE ====================================================================================================

    @Bean
    @ConfigurationProperties("app.datasource.context")
    @DependsOn("primaryJdbcClient") // The context datasource depends on the primary mariadb database existing.
    public DataSourceProperties contextDataSourceProperties() {
        System.out.println("Creating ContextDataSourceProperties");
        return new DataSourceProperties();

    }

    @Bean
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
    @DependsOn("contextDataSource")
	JdbcClient contextJdbcClient(@Qualifier("contextDataSource") DataSource dataSource) {
        System.out.println("In contextJdbcClient creation");
		return JdbcClient.create(dataSource);
	}

    // LOCATION DATABASE ====================================================================================================

    @Bean
    @ConfigurationProperties("app.datasource.location")
    @DependsOn("primaryJdbcClient") // The location datasource depends on the primary mariadb database existing.
    public DataSourceProperties locationDataSourceProperties() {
        System.out.println("Creating LocationDataSourceProperties");
        return new DataSourceProperties();
    }

    @Bean
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
    @DependsOn("locationDataSource")
	JdbcClient locationJdbcClient(@Qualifier("locationDataSource") DataSource dataSource) {
        System.out.println("In locationJdbcClient creation");
		return JdbcClient.create(dataSource);
	}

    // DATABASE INITIALIZATION ============================================================================================

    public class DatabaseInitializer {
        // Let us define a boolean value indicating whether the initialization was successful.
        private boolean initialized = false;
        private boolean failedInitialization = false;

    }

    // Call initializers for all data sources using DatabaseInitializer class.
    // jdbcClients do not need to be passed because they are defined via @Qualifier in the DatabaseInitializer class.
    @Bean
    @Scope("singleton")
    @DependsOn({"contextJdbcClient", "locationJdbcClient"})
    public DatabaseInitializer databaseInitializer(
        @Qualifier("contextJdbcClient") JdbcClient contextJdbcClient,
        @Qualifier("locationJdbcClient") JdbcClient locationJdbcClient
        ) 
    {
        DatabaseInitializer databaseInitializer = new DatabaseInitializer();
        System.out.println("In databaseInitializer");
        // I need these initializers to run and then detect if they completed successfully
        // if all completed successfully, then I can start the application.
        

        // Initialize context database
        ContextDatasourceInitializer contextInitializer = new ContextDatasourceInitializer(
            contextJdbcClient,
            this.applicationContext
        );
        
        boolean contextInitSuccess = contextInitializer.initialize();

        // Initialize location database
        LocationDatasourceInitializer locationInitializer = new LocationDatasourceInitializer(
            locationJdbcClient,
            this.applicationContext
            );

        boolean locationInitSuccess = locationInitializer.initialize();

        // Check if all initializations were successful
        if (contextInitSuccess && locationInitSuccess) {
            System.out.println("All initializers completed successfully. Application can start.");
        } else {
            throw new RuntimeException("Database initialization failed. Application startup aborted.");
        }
        return databaseInitializer;
    }
}