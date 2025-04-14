package org.envelope.helperservice.config.socket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class SessionConfig {
    @Bean
    public Map<String, String> dialogSessions() {
        return new HashMap<>();
    }
    @Bean
    public Map<String, WebSocketSession> sessions() {
        return new HashMap<>();
    }
}
