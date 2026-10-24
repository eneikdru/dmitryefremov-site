package com.dmitryefremov.site.agent.dto;

import com.dmitryefremov.site.canon.domain.CanonDomain;
import com.dmitryefremov.site.canon.domain.CanonType;

import java.time.OffsetDateTime;

public record ConceptUpdateRequestDto(
    String title,
    CanonType type,
    CanonDomain domain,
    String summary,
    String content,
    OffsetDateTime publishedAt
) {
    public ConceptUpdateRequestDto(String title, String summary, String content, OffsetDateTime publishedAt) {
        this(title, CanonType.ONTOLOGY, CanonDomain.AUTONOMOUS_ENGINEERING, summary, content, publishedAt);
    }
}
