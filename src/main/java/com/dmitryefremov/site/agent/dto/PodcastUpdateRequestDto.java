package com.dmitryefremov.site.agent.dto;

import java.time.OffsetDateTime;

public record PodcastUpdateRequestDto(
    String title,
    String thesis,
    String description,
    String audioUrl,
    String mediaLink,
    Integer durationSeconds,
    OffsetDateTime publishedAt
) {}
