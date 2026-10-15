package com.dmitryefremov.site.intake.service;

import com.dmitryefremov.site.intake.domain.IntakeRequest;
import com.dmitryefremov.site.intake.dto.IntakeResponseDto;
import com.dmitryefremov.site.intake.dto.IntakeSubmissionDto;
import com.dmitryefremov.site.intake.event.IntakeRequestCreatedEvent;
import com.dmitryefremov.site.intake.repository.IntakeRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;

@Service
public class IntakeService {

    private final IntakeRequestRepository intakeRequestRepository;
    private final ApplicationEventPublisher eventPublisher;
    private Clock clock;

    public IntakeService(IntakeRequestRepository intakeRequestRepository,
                         @Autowired(required = false) ApplicationEventPublisher eventPublisher,
                         @Autowired(required = false) Clock clock) {
        this.intakeRequestRepository = intakeRequestRepository;
        this.eventPublisher = eventPublisher;
        this.clock = clock != null ? clock : Clock.systemUTC();
    }

    public void setClock(Clock clock) {
        if (clock != null) {
            this.clock = clock;
        }
    }

    @Transactional
    public IntakeResponseDto submitIntake(IntakeSubmissionDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Intake request payload cannot be null");
        }

        // Standard domain validation
        validateSubmission(dto);

        // Human UI check vs A2A check
        if (!dto.isA2a()) {
            if (dto.getCaptchaToken() == null || dto.getCaptchaToken().isBlank()) {
                throw new IllegalArgumentException("Human UI verification (captchaToken) is required for standard intake submissions");
            }
        }

        OffsetDateTime now = OffsetDateTime.now(clock);

        IntakeRequest entity = new IntakeRequest(
                dto.getClientName().trim(),
                dto.getClientEmail().trim(),
                dto.getCompanyName() != null ? dto.getCompanyName().trim() : null,
                dto.getAuditPayload().trim(),
                now
        );

        IntakeRequest saved = intakeRequestRepository.save(entity);

        if (eventPublisher != null) {
            eventPublisher.publishEvent(new IntakeRequestCreatedEvent(saved));
        }

        String ackMessage = "Confidential audit intake request received successfully. Automatic acknowledgement dispatched to " + saved.getClientEmail();

        return new IntakeResponseDto(
                saved.getId(),
                "CONFIRMED",
                ackMessage,
                now
        );
    }

    private void validateSubmission(IntakeSubmissionDto dto) {
        if (dto.getClientName() == null || dto.getClientName().isBlank()) {
            throw new IllegalArgumentException("Client name is required");
        }
        if (dto.getClientEmail() == null || dto.getClientEmail().isBlank()) {
            throw new IllegalArgumentException("Client email is required");
        }
        if (!dto.getClientEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid client email address");
        }
        if (dto.getAuditPayload() == null || dto.getAuditPayload().isBlank()) {
            throw new IllegalArgumentException("Audit payload is required");
        }
    }
}
