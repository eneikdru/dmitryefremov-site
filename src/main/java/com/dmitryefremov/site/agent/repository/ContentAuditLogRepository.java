package com.dmitryefremov.site.agent.repository;

import com.dmitryefremov.site.agent.domain.ContentAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentAuditLogRepository extends JpaRepository<ContentAuditLog, Long> {
    List<ContentAuditLog> findByAgentId(Long agentId);
}
