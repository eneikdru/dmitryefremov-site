package com.dmitryefremov.site.knowledgebase.repository;

import com.dmitryefremov.site.knowledgebase.domain.KnowledgeBaseTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgeBaseTagRepository extends JpaRepository<KnowledgeBaseTag, Long> {

    List<KnowledgeBaseTag> findByConceptId(Long conceptId);

    List<KnowledgeBaseTag> findByTagNameIgnoreCase(String tagName);
}
