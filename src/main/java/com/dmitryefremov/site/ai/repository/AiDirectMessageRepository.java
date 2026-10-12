package com.dmitryefremov.site.ai.repository;

import com.dmitryefremov.site.ai.domain.AiDirectMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiDirectMessageRepository extends JpaRepository<AiDirectMessage, Long> {
    long deleteBySenderEmail(String senderEmail);
}
