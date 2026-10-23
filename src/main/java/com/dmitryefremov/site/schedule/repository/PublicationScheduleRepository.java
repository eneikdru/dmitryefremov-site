package com.dmitryefremov.site.schedule.repository;

import com.dmitryefremov.site.schedule.domain.PublicationPlatform;
import com.dmitryefremov.site.schedule.domain.PublicationSchedule;
import com.dmitryefremov.site.schedule.domain.PublicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicationScheduleRepository extends JpaRepository<PublicationSchedule, Long> {

    List<PublicationSchedule> findByPlatform(PublicationPlatform platform);

    List<PublicationSchedule> findByStatus(PublicationStatus status);

    List<PublicationSchedule> findByPlatformAndStatus(PublicationPlatform platform, PublicationStatus status);
}
