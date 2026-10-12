package com.dmitryefremov.site.compliance.repository;

import com.dmitryefremov.site.compliance.domain.AgentMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgentMessageRepository extends JpaRepository<AgentMessage, Long> {
    List<AgentMessage> findByUserIdentifier(String userIdentifier);
    long deleteByUserIdentifier(String userIdentifier);
}
