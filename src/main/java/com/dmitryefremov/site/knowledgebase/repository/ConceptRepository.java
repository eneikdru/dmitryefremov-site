package com.dmitryefremov.site.knowledgebase.repository;

import com.dmitryefremov.site.knowledgebase.domain.Concept;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConceptRepository extends JpaRepository<Concept, Long>, JpaSpecificationExecutor<Concept> {

    Optional<Concept> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Query("SELECT DISTINCT c.category FROM Concept c")
    List<String> findDistinctCategories();
}
