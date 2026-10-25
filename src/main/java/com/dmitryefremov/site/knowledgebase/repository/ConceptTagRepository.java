package com.dmitryefremov.site.knowledgebase.repository;

import com.dmitryefremov.site.knowledgebase.domain.ConceptTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConceptTagRepository extends JpaRepository<ConceptTag, Long> {

    @Query("SELECT DISTINCT ct.tagName FROM ConceptTag ct")
    List<String> findDistinctTagNames();
}
