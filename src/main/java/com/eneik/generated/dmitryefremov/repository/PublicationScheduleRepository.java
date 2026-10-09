package com.eneik.generated.dmitryefremov.repository;

import com.eneik.generated.dmitryefremov.domain.Platform;
import com.eneik.generated.dmitryefremov.domain.PublicationSchedule;
import com.eneik.generated.dmitryefremov.domain.PublicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicationScheduleRepository extends JpaRepository<PublicationSchedule, Long> {
    List<PublicationSchedule> findByPlatform(Platform platform);
    List<PublicationSchedule> findByStatus(PublicationStatus status);
}
