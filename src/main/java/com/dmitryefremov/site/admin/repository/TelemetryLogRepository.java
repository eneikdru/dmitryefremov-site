package com.dmitryefremov.site.admin.repository;

import com.dmitryefremov.site.admin.domain.TelemetryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TelemetryLogRepository extends JpaRepository<TelemetryLog, Long> {
    List<TelemetryLog> findByEventType(String eventType);
}
