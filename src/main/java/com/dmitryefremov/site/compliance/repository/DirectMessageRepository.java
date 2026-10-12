package com.dmitryefremov.site.compliance.repository;

import com.dmitryefremov.site.compliance.domain.DirectMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {

    List<DirectMessage> findByUserId(String userId);

    long deleteByUserId(String userId);
}
