package com.dmitryefremov.site.agent.repository;

import com.dmitryefremov.site.agent.domain.AgentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentOrderRepository extends JpaRepository<AgentOrder, Long> {
}
