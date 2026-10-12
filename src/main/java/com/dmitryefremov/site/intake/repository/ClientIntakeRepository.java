package com.dmitryefremov.site.intake.repository;

import com.dmitryefremov.site.intake.domain.ClientIntake;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientIntakeRepository extends JpaRepository<ClientIntake, Long> {
    List<ClientIntake> findByClientEmail(String clientEmail);
}
