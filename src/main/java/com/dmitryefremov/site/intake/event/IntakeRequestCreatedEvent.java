package com.dmitryefremov.site.intake.event;

import com.dmitryefremov.site.intake.domain.IntakeRequest;

public class IntakeRequestCreatedEvent {

    private final IntakeRequest intakeRequest;

    public IntakeRequestCreatedEvent(IntakeRequest intakeRequest) {
        this.intakeRequest = intakeRequest;
    }

    public IntakeRequest getIntakeRequest() {
        return intakeRequest;
    }
}
