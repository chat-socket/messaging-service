package com.vmtu.messagingservice.config;

import org.springframework.boot.autoconfigure.jms.artemis.ArtemisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.net.URI;

/**
 * @author mvu
 * @project chat-socket
 **/
@Configuration
@EnableWebSocketMessageBroker
@EnableConfigurationProperties(ArtemisProperties.class)
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final ArtemisProperties artemisProperties;

    public WebSocketConfig(ArtemisProperties artemisProperties) {
        this.artemisProperties = artemisProperties;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        var brokerUri = URI.create(artemisProperties.getBrokerUrl());
        registry.enableStompBrokerRelay("/queue", "/topic")
            .setRelayHost(brokerUri.getHost())
            .setRelayPort(brokerUri.getPort())
            .setSystemLogin(artemisProperties.getUser())
            .setSystemPasscode(artemisProperties.getPassword())
            .setClientLogin(artemisProperties.getUser())
            .setClientPasscode(artemisProperties.getPassword())
            .setUserDestinationBroadcast("/topic/unresolved-user")
            .setUserRegistryBroadcast("/topic/log-user-registry");

        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket");
        registry.addEndpoint("/websocket").withSockJS();
    }
}
