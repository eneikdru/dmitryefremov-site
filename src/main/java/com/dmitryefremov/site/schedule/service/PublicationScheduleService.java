package com.dmitryefremov.site.schedule.service;

import com.dmitryefremov.site.schedule.domain.PublicationSchedule;
import com.dmitryefremov.site.schedule.dto.PublicationScheduleDto;
import com.dmitryefremov.site.schedule.repository.PublicationScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicationScheduleService {

    private final PublicationScheduleRepository publicationScheduleRepository;

    public PublicationScheduleService(PublicationScheduleRepository publicationScheduleRepository) {
        this.publicationScheduleRepository = publicationScheduleRepository;
    }

    @Transactional(readOnly = true)
    public List<PublicationScheduleDto> getSchedules(String platform) {
        List<PublicationSchedule> schedules;
        if (platform == null || platform.isBlank()) {
            schedules = publicationScheduleRepository.findAllByOrderByScheduledAtAscIdAsc();
        } else {
            schedules = publicationScheduleRepository.findByPlatformIgnoreCaseOrderByScheduledAtAscIdAsc(platform.trim());
        }
        return schedules.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private PublicationScheduleDto toDto(PublicationSchedule schedule) {
        return new PublicationScheduleDto(
                schedule.getId(),
                schedule.getTitle(),
                schedule.getPlatform(),
                schedule.getStatus(),
                schedule.getTopicSummary(),
                schedule.getScheduledAt(),
                schedule.getCreatedAt()
        );
    }
}
