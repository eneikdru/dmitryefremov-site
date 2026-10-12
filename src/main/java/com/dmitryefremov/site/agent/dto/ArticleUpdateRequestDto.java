package com.dmitryefremov.site.agent.dto;

import java.time.OffsetDateTime;

public record ArticleUpdateRequestDto(
    String title,
    String summary,
    String content,
    OffsetDateTime publishedAt
) {}
