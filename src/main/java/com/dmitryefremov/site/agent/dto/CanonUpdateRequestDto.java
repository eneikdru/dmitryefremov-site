package com.dmitryefremov.site.agent.dto;

import com.dmitryefremov.site.canon.domain.CanonDomain;
import com.dmitryefremov.site.canon.domain.CanonType;

import java.time.OffsetDateTime;

public record CanonUpdateRequestDto(
    String title,
    CanonType type,
    CanonDomain domain,
    String summary,
    String content,
    OffsetDateTime publishedAt
) {}
