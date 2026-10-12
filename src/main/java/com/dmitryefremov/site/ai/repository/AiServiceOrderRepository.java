package com.dmitryefremov.site.ai.repository;

import com.dmitryefremov.site.ai.domain.AiServiceOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiServiceOrderRepository extends JpaRepository<AiServiceOrder, Long> {
    List<AiServiceOrder> findByUserEmail(String userEmail);
    long deleteByUserEmail(String userEmail);
}
