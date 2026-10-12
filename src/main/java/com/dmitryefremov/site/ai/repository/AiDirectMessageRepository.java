package com.dmitryefremov.site.ai.repository;

import com.dmitryefremov.site.ai.domain.AiDirectMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiDirectMessageRepository extends JpaRepository<AiDirectMessage, Long> {
    List<AiDirectMessage> findBySenderEmail(String senderEmail);
    long deleteBySenderEmail(String senderEmail);
}
