package com.dmitryefremov.site.telemetry.repository;

import com.dmitryefremov.site.telemetry.domain.PlatformTelemetryEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatformTelemetryEventRepository extends JpaRepository<PlatformTelemetryEvent, Long> {
}
