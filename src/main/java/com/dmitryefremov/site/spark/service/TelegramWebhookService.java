package com.dmitryefremov.site.spark.service;

import com.dmitryefremov.site.spark.domain.Spark;
import com.dmitryefremov.site.spark.dto.TelegramWebhookResponseDto;
import com.dmitryefremov.site.spark.mapper.TelegramSparkMapper;
import com.dmitryefremov.site.spark.repository.SparkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TelegramWebhookService {

    private final TelegramSparkMapper telegramSparkMapper;
    private final SparkRepository sparkRepository;

    public TelegramWebhookService(TelegramSparkMapper telegramSparkMapper, SparkRepository sparkRepository) {
        this.telegramSparkMapper = telegramSparkMapper;
        this.sparkRepository = sparkRepository;
    }

    @Transactional
    public TelegramWebhookResponseDto processWebhookPayload(String rawJsonPayload) {
        Spark mappedSpark = telegramSparkMapper.mapTelegramPayload(rawJsonPayload);

        if (mappedSpark.getTelegramMessageId() != null) {
            Optional<Spark> existingOpt = sparkRepository.findByTelegramMessageId(mappedSpark.getTelegramMessageId());
            if (existingOpt.isPresent()) {
                Spark existing = existingOpt.get();
                existing.setContent(mappedSpark.getContent());
                existing.setDomain(mappedSpark.getDomain());
                existing.setTags(mappedSpark.getTags());
                existing.setAuthor(mappedSpark.getAuthor());
                existing.setPublishedAt(mappedSpark.getPublishedAt());
                if (mappedSpark.getTelegramChatId() != null) {
                    existing.setTelegramChatId(mappedSpark.getTelegramChatId());
                }
                Spark saved = sparkRepository.save(existing);
                return new TelegramWebhookResponseDto(true, "Spark updated successfully", saved);
            }
        }

        Spark saved = sparkRepository.save(mappedSpark);
        return new TelegramWebhookResponseDto(true, "Spark ingested successfully", saved);
    }
}
