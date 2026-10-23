package com.dmitryefremov.site.intake.service;

import com.dmitryefremov.site.intake.domain.ClientIntake;
import com.dmitryefremov.site.intake.dto.ClientIntakeRequestDto;
import com.dmitryefremov.site.intake.dto.ClientIntakeResponseDto;
import com.dmitryefremov.site.intake.repository.ClientIntakeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
public class ClientIntakeService {

    private static final Logger logger = LoggerFactory.getLogger(ClientIntakeService.class);

    private final ClientIntakeRepository clientIntakeRepository;
    private final Clock clock;

    @Autowired
    public ClientIntakeService(ClientIntakeRepository clientIntakeRepository,
                               @Autowired(required = false) Clock clock) {
        this.clientIntakeRepository = clientIntakeRepository;
        this.clock = clock != null ? clock : Clock.systemUTC();
    }

    public ClientIntakeService(ClientIntakeRepository clientIntakeRepository) {
        this(clientIntakeRepository, Clock.systemUTC());
    }

    @Transactional
    public ClientIntakeResponseDto submitIntake(ClientIntakeRequestDto request) {
        ClientIntake intake = new ClientIntake(
                request.getClientName(),
                request.getClientEmail(),
                request.getCompanyName(),
                request.getWishlistPayload(),
                clock
        );

        ClientIntake saved = clientIntakeRepository.save(intake);

        logger.info("Successfully received client intake request id: {}", saved.getId());

        ClientIntakeResponseDto response = new ClientIntakeResponseDto();
        response.setId(saved.getId());
        response.setClientType(request.getClientType() != null ? request.getClientType() : "HUMAN");
        response.setClientName(saved.getClientName());
        response.setClientEmail(saved.getClientEmail());
        response.setCompanyName(saved.getCompanyName());
        response.setWishlistPayload(saved.getWishlistPayload());
        response.setStatus(saved.getStatus());
        response.setAgentMetadata(request.getAgentMetadata());
        response.setSource(request.getSource() != null ? request.getSource() : "WEB_PORTAL");
        response.setCreatedAt(saved.getCreatedAt());
        response.setUpdatedAt(saved.getUpdatedAt());

        return response;
    }

    public Clock getClock() {
        return clock;
    }
}
