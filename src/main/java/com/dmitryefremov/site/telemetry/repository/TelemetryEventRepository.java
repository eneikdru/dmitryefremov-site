package com.dmitryefremov.site.telemetry.repository;

import com.dmitryefremov.site.telemetry.domain.TelemetryEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TelemetryEventRepository extends JpaRepository<TelemetryEvent, Long> {

    List<TelemetryEvent> findByEventTypeOrderByCreatedAtDesc(String eventType);

    List<TelemetryEvent> findAllByOrderByCreatedAtDesc();
}
