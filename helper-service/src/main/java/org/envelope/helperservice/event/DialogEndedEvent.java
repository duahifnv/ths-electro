package org.envelope.helperservice.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DialogEndedEvent extends ApplicationEvent {
    private final String initiatorId;
    private final String companionId;
    public DialogEndedEvent(Object source, String initiatorId, String companionId) {
        super(source);
        this.initiatorId = initiatorId;
        this.companionId = companionId;
    }
}
