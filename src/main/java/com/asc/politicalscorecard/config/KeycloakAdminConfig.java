package com.asc.politicalscorecard.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class KeycloakAdminConfig {

    @Value("${app.keycloak-admin.server-url}")
    private String serverUrl;

    @Value("${app.keycloak-admin.realm}")
    private String realm;

    @Value("${app.keycloak-admin.client-id}")
    private String clientId;

    @Value("${app.keycloak-admin.client-secret}")
    private String clientSecret;

    public String getServerUrl() {
        return serverUrl;
    }

    public String getRealm() {
        return realm;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    @Bean("keycloakAdminRestClient")
    public RestClient keycloakAdminRestClient() {
        return RestClient.builder()
                .baseUrl(serverUrl)
                .build();
    }
}
