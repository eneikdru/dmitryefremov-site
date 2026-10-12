package com.dmitryefremov.site.compliance.controller;

import com.dmitryefremov.site.compliance.dto.ErasureResultDto;
import com.dmitryefremov.site.compliance.dto.UserDataExportDto;
import com.dmitryefremov.site.compliance.service.UserDataPrivacyService;
import com.dmitryefremov.site.podcast.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/compliance")
public class ComplianceController {

    private final UserDataPrivacyService privacyService;
    private final Clock clock;

    public ComplianceController(UserDataPrivacyService privacyService, Clock clock) {
        this.privacyService = privacyService;
        this.clock = clock;
    }

    @GetMapping("/export")
    public ResponseEntity<UserDataExportDto> exportUserData(@RequestParam String userIdentifier) {
        UserDataExportDto exportDto = privacyService.exportUserData(userIdentifier);
        return ResponseEntity.ok(exportDto);
    }

    @DeleteMapping("/erasure")
    public ResponseEntity<ErasureResultDto> eraseUserData(@RequestParam String userIdentifier) {
        ErasureResultDto erasureResult = privacyService.eraseUserData(userIdentifier);
        return ResponseEntity.ok(erasureResult);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                OffsetDateTime.now(clock)
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
