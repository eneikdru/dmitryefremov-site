package com.dmitryefremov.site.telemetry.repository;

import com.dmitryefremov.site.telemetry.domain.PlatformTelemetryMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface PlatformTelemetryMetricRepository extends JpaRepository<PlatformTelemetryMetric, String> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE platform_telemetry_metrics SET counter = counter + :value, updated_at = :now WHERE metric_type = :metricType", nativeQuery = true)
    int incrementMetric(@Param("metricType") String metricType, @Param("value") Double value, @Param("now") OffsetDateTime now);
}
