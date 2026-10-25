package com.dmitryefremov.site.knowledgebase.repository;

import com.dmitryefremov.site.knowledgebase.domain.KnowledgeBaseTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgeBaseTagRepository extends JpaRepository<KnowledgeBaseTag, Long> {

    @Query("SELECT DISTINCT t.tagName FROM KnowledgeBaseTag t ORDER BY t.tagName")
    List<String> findDistinctTagNames();

    List<KnowledgeBaseTag> findByConceptId(Long conceptId);
}
