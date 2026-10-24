package com.dmitryefremov.site.agent.dto;

import java.time.OffsetDateTime;

public record EssayUpdateRequestDto(
    String title,
    String summary,
    String content,
    String curatedBy,
    OffsetDateTime publishedAt
) {}
