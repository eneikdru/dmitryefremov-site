package com.dmitryefremov.site.agent.repository;

import com.dmitryefremov.site.agent.domain.AgentInteractionTelemetry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface AgentInteractionTelemetryRepository extends JpaRepository<AgentInteractionTelemetry, String> {

    @Modifying
    @Query("UPDATE AgentInteractionTelemetry t SET t.counter = t.counter + 1, t.updatedAt = :now WHERE t.interactionType = :interactionType")
    int incrementCounter(@Param("interactionType") String interactionType, @Param("now") OffsetDateTime now);
}
