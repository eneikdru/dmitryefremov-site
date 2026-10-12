package com.dmitryefremov.site.ai.repository;

import com.dmitryefremov.site.ai.domain.AiServiceOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiServiceOrderRepository extends JpaRepository<AiServiceOrder, Long> {
    long deleteByUserEmail(String userEmail);
}
