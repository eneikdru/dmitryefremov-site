package com.dmitryefremov.site.schedule.repository;

import com.dmitryefremov.site.schedule.domain.PublicationSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicationScheduleRepository extends JpaRepository<PublicationSchedule, Long> {

    List<PublicationSchedule> findAllByOrderByScheduledAtAscIdAsc();

    List<PublicationSchedule> findByPlatformIgnoreCaseOrderByScheduledAtAscIdAsc(String platform);
}
