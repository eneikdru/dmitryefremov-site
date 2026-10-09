package com.eneik.generated.dmitryefremovsite.repository;

import com.eneik.generated.dmitryefremovsite.domain.Platform;
import com.eneik.generated.dmitryefremovsite.domain.PublicationSchedule;
import com.eneik.generated.dmitryefremovsite.domain.PublicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicationScheduleRepository extends JpaRepository<PublicationSchedule, Long> {
    List<PublicationSchedule> findByPlatform(Platform platform);
    List<PublicationSchedule> findByStatus(PublicationStatus status);
}
