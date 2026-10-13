package com.dmitryefremov.site.telemetry.repository;

import com.dmitryefremov.site.telemetry.domain.ScrollytellingProgression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public interface ScrollytellingProgressionRepository extends JpaRepository<ScrollytellingProgression, Long> {

    Optional<ScrollytellingProgression> findByResourceId(String resourceId);

    @Modifying
    @Query(value = "UPDATE scrollytelling_progression " +
                   "SET total_depth = total_depth + :depth, " +
                   "max_depth = CASE WHEN :depth > max_depth THEN :depth ELSE max_depth END, " +
                   "event_count = event_count + 1, " +
                   "updated_at = :now " +
                   "WHERE resource_id = :resourceId",
           nativeQuery = true)
    int incrementProgressionAtomic(@Param("resourceId") String resourceId,
                                  @Param("depth") double depth,
                                  @Param("now") OffsetDateTime now);
}
