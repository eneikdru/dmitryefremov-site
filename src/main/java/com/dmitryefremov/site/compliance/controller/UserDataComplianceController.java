package com.dmitryefremov.site.compliance.controller;

import com.dmitryefremov.site.compliance.dto.*;
import com.dmitryefremov.site.compliance.service.UserDataComplianceService;
import com.dmitryefremov.site.podcast.dto.ErrorResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.time.OffsetDateTime;

@RestController("siteUserDataComplianceController")
public class UserDataComplianceController {

    private final UserDataComplianceService complianceService;
    private final Clock clock;

    @Autowired
    public UserDataComplianceController(@Qualifier("siteUserDataComplianceService") UserDataComplianceService complianceService,
                                        @Autowired(required = false) Clock clock) {
        this.complianceService = complianceService;
        this.clock = clock != null ? clock : (complianceService != null ? complianceService.getClock() : Clock.systemUTC());
    }

    public UserDataComplianceController(UserDataComplianceService complianceService) {
        this(complianceService, null);
    }

    @GetMapping({"/api/v1/compliance/export", "/api/v1/user-data/export"})
    public ResponseEntity<UserDataExportDto> exportUserData(@RequestParam("userId") String userId) {
        UserDataExportDto exportData = complianceService.exportUserData(userId);
        return ResponseEntity.ok(exportData);
    }

    @DeleteMapping({"/api/v1/compliance/erasure", "/api/v1/user-data"})
    public ResponseEntity<ErasureResponseDto> eraseUserData(@RequestParam("userId") String userId) {
        ErasureResponseDto result = complianceService.eraseUserData(userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/api/v1/orders")
    public ResponseEntity<ServiceOrderDto> createOrder(@RequestBody ServiceOrderDto request) {
        ServiceOrderDto created = complianceService.createServiceOrder(
                request.getUserId(),
                request.getServiceType(),
                request.getDetails()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/api/v1/messages")
    public ResponseEntity<DirectMessageDto> createMessage(@RequestBody DirectMessageDto request) {
        DirectMessageDto created = complianceService.createDirectMessage(
                request.getUserId(),
                request.getContent()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
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
