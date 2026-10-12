package com.dmitryefremov.site.spark.dto;

import com.dmitryefremov.site.spark.domain.Spark;

import java.time.OffsetDateTime;

public record SparkDto(
        Long id,
        Long telegramMessageId,
        Long telegramChatId,
        String content,
        String domain,
        String tags,
        String author,
        OffsetDateTime publishedAt,
        OffsetDateTime createdAt
) {
    public static SparkDto fromEntity(Spark entity) {
        if (entity == null) {
            return null;
        }
        return new SparkDto(
                entity.getId(),
                entity.getTelegramMessageId(),
                entity.getTelegramChatId(),
                entity.getContent(),
                entity.getDomain(),
                entity.getTags(),
                entity.getAuthor(),
                entity.getPublishedAt(),
                entity.getCreatedAt()
        );
    }
}
