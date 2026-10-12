package com.dmitryefremov.site.agent.repository;

import com.dmitryefremov.site.agent.domain.AgentApiToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgentApiTokenRepository extends JpaRepository<AgentApiToken, Long> {
    Optional<AgentApiToken> findByAgentName(String agentName);
}
