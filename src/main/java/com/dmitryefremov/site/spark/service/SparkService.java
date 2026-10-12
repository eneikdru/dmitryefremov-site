package com.dmitryefremov.site.spark.service;

import com.dmitryefremov.site.spark.domain.Spark;
import com.dmitryefremov.site.spark.dto.SparkDto;
import com.dmitryefremov.site.spark.mapper.TelegramSparkMapper;
import com.dmitryefremov.site.spark.repository.SparkRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SparkService {

    private final SparkRepository sparkRepository;
    private final TelegramSparkMapper telegramSparkMapper;

    public SparkService(SparkRepository sparkRepository, TelegramSparkMapper telegramSparkMapper) {
        this.sparkRepository = sparkRepository;
        this.telegramSparkMapper = telegramSparkMapper;
    }

    public SparkDto processTelegramWebhook(String rawJsonPayload) {
        Spark mappedSpark = telegramSparkMapper.mapTelegramPayload(rawJsonPayload);
        Spark sparkToSave = mappedSpark;

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
                sparkToSave = existing;
            }
        }

        Spark saved = sparkRepository.save(sparkToSave);
        return SparkDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public Page<SparkDto> getSparks(int page, int size, String domain) {
        PageRequest pageRequest = PageRequest.of(page, Math.min(size, 100));
        Page<Spark> sparksPage;
        if (domain != null && !domain.isBlank()) {
            sparksPage = sparkRepository.findByDomainOrderByPublishedAtDesc(domain, pageRequest);
        } else {
            sparksPage = sparkRepository.findAllByOrderByPublishedAtDesc(pageRequest);
        }
        return sparksPage.map(SparkDto::fromEntity);
    }

    @Transactional(readOnly = true)
    public List<SparkDto> getAllSparks() {
        return sparkRepository.findAllByOrderByPublishedAtDesc(PageRequest.of(0, 100))
                .getContent()
                .stream()
                .map(SparkDto::fromEntity)
                .toList();
    }
}
