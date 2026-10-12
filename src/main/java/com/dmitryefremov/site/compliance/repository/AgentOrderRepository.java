package com.dmitryefremov.site.compliance.repository;

import com.dmitryefremov.site.compliance.domain.AgentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgentOrderRepository extends JpaRepository<AgentOrder, Long> {
    List<AgentOrder> findByUserIdentifier(String userIdentifier);
    long deleteByUserIdentifier(String userIdentifier);
}
