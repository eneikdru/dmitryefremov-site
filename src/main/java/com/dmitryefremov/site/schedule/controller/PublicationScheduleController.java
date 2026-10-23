package com.dmitryefremov.site.schedule.controller;

import com.dmitryefremov.site.schedule.dto.PublicationScheduleDto;
import com.dmitryefremov.site.schedule.service.PublicationScheduleService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class PublicationScheduleController {

    private final PublicationScheduleService publicationScheduleService;

    public PublicationScheduleController(PublicationScheduleService publicationScheduleService) {
        this.publicationScheduleService = publicationScheduleService;
    }

    @GetMapping(
            value = {"/schedule", "/schedules", "/publication-schedules"},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<PublicationScheduleDto>> getSchedules(
            @RequestParam(required = false) String platform
    ) {
        List<PublicationScheduleDto> schedules = publicationScheduleService.getSchedules(platform);
        return ResponseEntity.ok(schedules);
    }
}
