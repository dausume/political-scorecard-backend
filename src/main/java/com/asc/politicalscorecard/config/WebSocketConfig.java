package com.asc.politicalscorecard.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket Configuration for real-time debate messaging
 * Uses STOMP over WebSocket protocol
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${app.cors.allowed-origins:http://localhost:4200,https://localhost:4200}")
    private String allowedOriginsConfig;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple in-memory message broker
        // Prefix for messages FROM server TO client
        config.enableSimpleBroker("/topic");

        // Prefix for messages FROM client TO server
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register STOMP endpoint that clients will connect to
        // Allowed origins are loaded from the same config as CORS (app.cors.allowed-origins)
        String[] origins = allowedOriginsConfig.split(",");
        registry.addEndpoint("/ws-debate")
            .setAllowedOrigins(origins)
            .withSockJS(); // Fallback option for browsers that don't support WebSocket
    }
}
