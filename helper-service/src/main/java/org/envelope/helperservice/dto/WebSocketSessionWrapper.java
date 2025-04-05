package org.envelope.helperservice.dto;

import org.springframework.web.socket.WebSocketSession;

public record WebSocketSessionWrapper(
        WebSocketSession session,
        Long helperId,
        Long userId) {
}