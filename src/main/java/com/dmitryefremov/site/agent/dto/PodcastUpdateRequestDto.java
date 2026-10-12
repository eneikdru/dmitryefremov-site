package com.dmitryefremov.site.agent.dto;

import com.dmitryefremov.site.podcast.dto.PodcastQuoteDto;
import com.dmitryefremov.site.podcast.dto.PodcastTimecodeDto;

import java.time.OffsetDateTime;
import java.util.List;

public record PodcastUpdateRequestDto(
    String title,
    String thesis,
    String description,
    String audioUrl,
    String mediaLink,
    String youtubeUrl,
    String telegramUrl,
    String spotifyUrl,
    String applePodcastsUrl,
    String platforms,
    String conceptLinks,
    Integer durationSeconds,
    OffsetDateTime publishedAt,
    List<PodcastTimecodeDto> timecodes,
    List<PodcastQuoteDto> quotes
) {
    public PodcastUpdateRequestDto(String title, String thesis, String description, String audioUrl, String mediaLink, Integer durationSeconds, OffsetDateTime publishedAt) {
        this(title, thesis, description, audioUrl, mediaLink, null, null, null, null, null, null, durationSeconds, publishedAt, null, null);
    }
}
