package org.envelope.helperservice.config.socket;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envelope.helperservice.service.SessionService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ActionChannelInterceptor implements ChannelInterceptor {
    private final SessionService sessionService;
    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String sessionId = accessor.getSessionId();
        String role = (String) accessor.getSessionAttributes().get("role");
        StompCommand command = accessor.getCommand();

        if (role == null || command == null) return message;
        switch (command) {
            case SUBSCRIBE -> {
                if (role.equals("user") || (role.equals("helper") && sessionService.subscribeClient(sessionId, role))) {
                    return message;
                }
            }
            case UNSUBSCRIBE -> sessionService.unsubscribeClient(sessionId, role);
            case DISCONNECT -> {
                sessionService.unsubscribeClient(sessionId, role);
                sessionService.removeClientSession(sessionId);
            }
            default -> {
                return message;
            }
        }
        return null;
    }
}