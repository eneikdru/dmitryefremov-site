package com.dmitryefremov.site.agent.repository;

import com.dmitryefremov.site.agent.domain.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {

    Optional<ApiKey> findByKeyPrefix(String keyPrefix);

    List<ApiKey> findByClientName(String clientName);

    List<ApiKey> findByStatus(String status);
}
