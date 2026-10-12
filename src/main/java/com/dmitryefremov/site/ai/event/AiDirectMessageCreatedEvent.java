package com.dmitryefremov.site.ai.event;

import com.dmitryefremov.site.ai.domain.AiDirectMessage;

public class AiDirectMessageCreatedEvent {

    private final AiDirectMessage directMessage;

    public AiDirectMessageCreatedEvent(AiDirectMessage directMessage) {
        this.directMessage = directMessage;
    }

    public AiDirectMessage getDirectMessage() {
        return directMessage;
    }
}
