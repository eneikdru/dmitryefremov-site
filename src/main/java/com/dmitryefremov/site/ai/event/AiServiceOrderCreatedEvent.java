package com.dmitryefremov.site.ai.event;

import com.dmitryefremov.site.ai.domain.AiServiceOrder;

public class AiServiceOrderCreatedEvent {

    private final AiServiceOrder serviceOrder;

    public AiServiceOrderCreatedEvent(AiServiceOrder serviceOrder) {
        this.serviceOrder = serviceOrder;
    }

    public AiServiceOrder getServiceOrder() {
        return serviceOrder;
    }
}
