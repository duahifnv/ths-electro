package org.envelope.helperservice.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class WaitingCountEvent extends ApplicationEvent {
    private final int waitingCount;
    public WaitingCountEvent(Object source, int waitingCount) {
        super(source);
        this.waitingCount = waitingCount;
    }
}
