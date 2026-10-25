package com.dmitryefremov.site.knowledgebase.repository;

import com.dmitryefremov.site.knowledgebase.domain.KnowledgeBaseConcept;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KnowledgeBaseConceptRepository extends JpaRepository<KnowledgeBaseConcept, Long> {

    Optional<KnowledgeBaseConcept> findBySlug(String slug);

    Page<KnowledgeBaseConcept> findByCategoryIgnoreCase(String category, Pageable pageable);

    @Query("SELECT c FROM KnowledgeBaseConcept c WHERE " +
           "LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.content) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.category) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<KnowledgeBaseConcept> searchByQuery(@Param("query") String query);
}
