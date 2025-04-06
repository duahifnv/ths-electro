package org.envelope.helperservice.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

@Data
@Builder
public class SocketDialog {
    private WebSocketSession session;
    private String helperId;
    private Long userId;
}