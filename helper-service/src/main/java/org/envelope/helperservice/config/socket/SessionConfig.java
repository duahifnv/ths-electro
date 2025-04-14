package org.envelope.helperservice.config.socket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class SessionConfig {
    /**
     * <p><b>key</b> - user sessionId</p>
     * <p><b>value</b> - helper sessionId</p>
     */
    @Bean
    public Map<String, String> dialogSessions() {
        return new HashMap<>();
    }
    /**
     * <p><b>key</b> - sessionId</p>
     * <p><b>value</b> - session by sessionId</p>
     */
    @Bean
    public Map<String, WebSocketSession> sessions() {
        return new HashMap<>();
    }
    /**
     * <p><b>key</b> - sessionId</p>
     * <p><b>value</b> - subscriptionId</p>
     */
    @Bean
    public Map<String, String> usersPrivateSubscriptions() {
        return new HashMap<>();
    }
    /**
     * <p><b>key</b> - sessionId</p>
     * <p><b>value</b> - subscriptionId</p>
     */
    @Bean
    public Map<String, String> helpersPrivateSubscriptions() {
        return new HashMap<>();
    }
}
