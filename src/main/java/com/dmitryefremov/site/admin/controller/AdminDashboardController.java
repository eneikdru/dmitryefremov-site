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

    @RequestMapping(value = {"/gdpr/export", "/gdpr/export/{userId}", "/v1/admin/gdpr/export/{userId}", "/v1/admin/gdpr/export"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<UserDataExportDto> exportUserData(
            @PathVariable(value = "userId", required = false) String pathUserId,
            @RequestParam(value = "userId", required = false) String queryUserId,
            @RequestParam(value = "email", required = false) String email) {
        String identifier = (pathUserId != null && !pathUserId.isBlank()) ? pathUserId
                : ((email != null && !email.isBlank()) ? email : queryUserId);
        UserDataExportDto exportData = complianceService.exportUserData(identifier);
        return ResponseEntity.ok(exportData);
    }

    @RequestMapping(value = {"/gdpr/erasure", "/gdpr/erasure/{userId}", "/v1/admin/gdpr/erasure/{userId}", "/v1/admin/gdpr/erasure"}, method = {RequestMethod.DELETE, RequestMethod.POST})
    public ResponseEntity<ErasureResponseDto> eraseUserData(
            @PathVariable(value = "userId", required = false) String pathUserId,
            @RequestParam(value = "userId", required = false) String queryUserId,
            @RequestParam(value = "email", required = false) String email) {
        String identifier = (pathUserId != null && !pathUserId.isBlank()) ? pathUserId
                : ((email != null && !email.isBlank()) ? email : queryUserId);
        ErasureResponseDto result = complianceService.eraseUserData(identifier);
        return ResponseEntity.ok(result);
    }
}
