package ch.uzh.ifi.hase.soprafs21.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue/specific-game");
        config.setApplicationDestinationPrefixes("/app");
        //to debug cors: config.enableSimpleBroker("/")
        //to debug cors: config.enableSimpleBroker("/game/queue/specific-game")
        config.setUserDestinationPrefix("/game");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/gs-guide-websocket")
                .setAllowedOrigins("https://sopra-fs21-group-11-client.herokuapp.com", "http://localhost:3000")
                .withSockJS();
    }
}
