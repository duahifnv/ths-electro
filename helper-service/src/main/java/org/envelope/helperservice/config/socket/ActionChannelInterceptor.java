package org.envelope.helperservice.config.socket;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envelope.helperservice.dto.Role;
import org.envelope.helperservice.exception.ClientException;
import org.envelope.helperservice.service.SessionService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class ActionChannelInterceptor implements ChannelInterceptor {
    private final SessionService sessionService;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String sessionId = accessor.getSessionId();
        Role role;
        try {
            role = sessionService.getSessionAttribute("role", accessor, Role.class);
        } catch (RuntimeException e) {
            throw new ClientException("Отсутствуют требуемые роли");
        }
        StompCommand command = accessor.getCommand();
        switch (command) {
            case SUBSCRIBE -> {
                if (Objects.equals(accessor.getDestination(), "/user/queue/private")) {
                    try {
                        sessionService.subscribeToPrivate(sessionId, role, accessor.getSubscriptionId());
                    } catch (RuntimeException e) {
                        log.warn(e.getMessage());
                        return null;
                    }
                }
            }
            case UNSUBSCRIBE -> {
                String privateSubscriptionId = sessionService.getPrivateSubscriptionId(sessionId, role);
                if (Objects.equals(accessor.getSubscriptionId(), privateSubscriptionId)) {
                    sessionService.unsubscribeFromPrivate(sessionId, role);
                }
            }
            case DISCONNECT -> {
                sessionService.unsubscribeFromPrivate(sessionId, role);
                sessionService.removeClientSession(sessionId);
                return null;
            }
            case null, default -> {}
        }
        return message;
    }
}