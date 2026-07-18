package com.asc.politicalscorecard.databases.datasourceinitializers.scoringdatasource;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * The three group↔instance authority tables: the Polari-instance
 * registry, the PSC side of group↔instance bindings, and term
 * provenance ("asserted by group X via instance Y"). Also seeds the
 * default Polari instance from app.polari.api-url so the previously
 * hardwired single instance becomes the first registry row.
 */
public class AuthorityTablesInitializer {

    private final JdbcClient scoringJdbcClient;
    private final String defaultPolariApiUrl;

    public AuthorityTablesInitializer(
            @Qualifier("scoringJdbcClient") JdbcClient scoringJdbcClient,
            String defaultPolariApiUrl) {
        this.scoringJdbcClient = scoringJdbcClient;
        this.defaultPolariApiUrl = defaultPolariApiUrl;
    }

    public void initializeTables() {
        System.out.println("Initializing authority tables (polari_instance, group_instance_binding, term_provenance)...");

        scoringJdbcClient.sql(
            "CREATE TABLE IF NOT EXISTS polari_instance (" +
            "id VARCHAR(36) PRIMARY KEY, " +
            "name VARCHAR(200) NOT NULL UNIQUE, " +
            "base_url VARCHAR(2000) NOT NULL, " +
            "description TEXT, " +
            "status VARCHAR(20) DEFAULT 'active', " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")"
        ).update();

        scoringJdbcClient.sql(
            "CREATE TABLE IF NOT EXISTS group_instance_binding (" +
            "id VARCHAR(36) PRIMARY KEY, " +
            "group_name VARCHAR(200) NOT NULL, " +
            "group_type VARCHAR(50), " +
            "instance_name VARCHAR(200) NOT NULL, " +
            "polari_binding_name VARCHAR(500) NOT NULL, " +
            "status VARCHAR(20) DEFAULT 'active', " +
            "created_by_sub VARCHAR(64), " +
            "created_by_username VARCHAR(200), " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "confirmed_at VARCHAR(50), " +
            "UNIQUE KEY uq_group_instance (group_name, instance_name)" +
            ")"
        ).update();

        scoringJdbcClient.sql(
            "CREATE TABLE IF NOT EXISTS term_provenance (" +
            "id VARCHAR(36) PRIMARY KEY, " +
            "term_id VARCHAR(36), " +
            "term_name VARCHAR(500) NOT NULL, " +
            "context_name VARCHAR(500), " +
            "group_name VARCHAR(200) NOT NULL, " +
            "instance_name VARCHAR(200) NOT NULL, " +
            "signal_name VARCHAR(1000), " +
            "requested_by VARCHAR(200), " +
            "verdict_json LONGTEXT, " +
            "admitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")"
        ).update();

        // The previously hardwired single Polari becomes the first
        // registry row (idempotent on the UNIQUE name).
        scoringJdbcClient.sql(
            "INSERT IGNORE INTO polari_instance (id, name, base_url, description, status) " +
            "VALUES (?, ?, ?, ?, 'active')"
        ).params(java.util.List.of(
            java.util.UUID.randomUUID().toString(),
            "polari-default",
            defaultPolariApiUrl,
            "Default Polari instance (seeded from app.polari.api-url)"
        )).update();

        System.out.println("Authority tables initialized successfully.");
    }
}
