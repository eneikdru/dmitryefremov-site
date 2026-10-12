package com.dmitryefremov.site.agent.repository;

import com.dmitryefremov.site.agent.domain.AgentMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentMessageRepository extends JpaRepository<AgentMessage, Long> {
    long deleteBySenderEmail(String senderEmail);
}
