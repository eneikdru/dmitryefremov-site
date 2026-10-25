package com.dmitryefremov.site.knowledgebase.repository;

import com.dmitryefremov.site.knowledgebase.domain.AppliedModelConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppliedModelConfigurationRepository extends JpaRepository<AppliedModelConfiguration, Long> {

    List<AppliedModelConfiguration> findByConceptId(Long conceptId);

    List<AppliedModelConfiguration> findByConceptIdAndActiveTrue(Long conceptId);

    List<AppliedModelConfiguration> findByModelName(String modelName);
}
