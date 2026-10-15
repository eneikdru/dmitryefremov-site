package com.dmitryefremov.site.intake.repository;

import com.dmitryefremov.site.intake.domain.IntakeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntakeRequestRepository extends JpaRepository<IntakeRequest, Long> {
    List<IntakeRequest> findByClientEmail(String clientEmail);
    long deleteByClientEmail(String clientEmail);
}
