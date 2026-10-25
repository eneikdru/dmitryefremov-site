package com.dmitryefremov.site.knowledgebase.repository;

import com.dmitryefremov.site.knowledgebase.domain.KnowledgeBaseConcept;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KnowledgeBaseConceptRepository extends JpaRepository<KnowledgeBaseConcept, Long>, JpaSpecificationExecutor<KnowledgeBaseConcept> {

    Optional<KnowledgeBaseConcept> findBySlug(String slug);

    @Query("SELECT DISTINCT k.category FROM KnowledgeBaseConcept k ORDER BY k.category")
    List<String> findDistinctCategories();

    boolean existsBySlug(String slug);
}
