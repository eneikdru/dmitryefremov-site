package com.dmitryefremov.site.intake.service;

import com.dmitryefremov.site.intake.domain.ClientIntake;
import com.dmitryefremov.site.intake.dto.ClientIntakeRequestDto;
import com.dmitryefremov.site.intake.dto.ClientIntakeResponseDto;
import com.dmitryefremov.site.intake.repository.ClientIntakeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
public class ClientIntakeService {

    private final ClientIntakeRepository clientIntakeRepository;
    private final Clock clock;

    public ClientIntakeService(ClientIntakeRepository clientIntakeRepository, Clock clock) {
        this.clientIntakeRepository = clientIntakeRepository;
        this.clock = clock;
    }

    @Transactional
    public ClientIntakeResponseDto createIntake(ClientIntakeRequestDto requestDto) {
        ClientIntake intake = new ClientIntake(
                requestDto.getClientName(),
                requestDto.getClientEmail(),
                requestDto.getCompanyName(),
                requestDto.getWishlistPayload(),
                clock
        );

        ClientIntake saved = clientIntakeRepository.save(intake);

        return new ClientIntakeResponseDto(
                saved.getId(),
                saved.getClientName(),
                saved.getClientEmail(),
                saved.getCompanyName(),
                saved.getStatus(),
                saved.getCreatedAt()
        );
    }
}
