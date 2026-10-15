package com.dmitryefremov.site.admin.controller;

import com.dmitryefremov.site.admin.domain.TelemetryLog;
import com.dmitryefremov.site.admin.repository.TelemetryLogRepository;
import com.dmitryefremov.site.compliance.dto.ErasureResponseDto;
import com.dmitryefremov.site.compliance.dto.UserDataExportDto;
import com.dmitryefremov.site.compliance.service.UserDataComplianceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    private final TelemetryLogRepository telemetryLogRepository;
    private final UserDataComplianceService complianceService;

    @Autowired
    public AdminDashboardController(
            TelemetryLogRepository telemetryLogRepository,
            @Qualifier("siteUserDataComplianceService") UserDataComplianceService complianceService) {
        this.telemetryLogRepository = telemetryLogRepository;
        this.complianceService = complianceService;
    }

    @GetMapping
    public ResponseEntity<List<TelemetryLog>> getTelemetryData() {
        return ResponseEntity.ok(telemetryLogRepository.findAll());
    }

    @PostMapping("/gdpr/export")
    public ResponseEntity<UserDataExportDto> exportUserData(
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "email", required = false) String email) {
        String identifier = (email != null && !email.isBlank()) ? email : userId;
        UserDataExportDto exportData = complianceService.exportUserData(identifier);
        return ResponseEntity.ok(exportData);
    }

    @DeleteMapping("/gdpr/erasure")
    public ResponseEntity<ErasureResponseDto> eraseUserData(
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "email", required = false) String email) {
        String identifier = (email != null && !email.isBlank()) ? email : userId;
        ErasureResponseDto result = complianceService.eraseUserData(identifier);
        return ResponseEntity.ok(result);
    }
}
