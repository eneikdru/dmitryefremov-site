package com.dmitryefremov.site.spark.service;

import com.dmitryefremov.site.spark.domain.Spark;
import com.dmitryefremov.site.spark.dto.SparkDto;
import com.dmitryefremov.site.spark.dto.SparkPageDto;
import com.dmitryefremov.site.spark.repository.SparkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service("siteSparkService")
@Transactional(readOnly = true)
public class SparkService {

    private final SparkRepository sparkRepository;

    @Autowired
    public SparkService(SparkRepository sparkRepository) {
        this.sparkRepository = sparkRepository;
    }

    public SparkPageDto getSparks(int page, int size, String domain) {
        Pageable pageable = PageRequest.of(
                Math.max(0, page),
                Math.max(1, Math.min(100, size)),
                Sort.by(Sort.Direction.DESC, "publishedAt", "createdAt")
        );

        Page<Spark> sparkPage;
        if (domain != null && !domain.isBlank()) {
            sparkPage = sparkRepository.findByDomain(domain, pageable);
        } else {
            sparkPage = sparkRepository.findAll(pageable);
        }

        List<SparkDto> items = sparkPage.getContent().stream()
                .map(SparkDto::fromEntity)
                .toList();

        return new SparkPageDto(
                items,
                sparkPage.getNumber(),
                sparkPage.getTotalPages(),
                sparkPage.getTotalElements()
        );
    }

    public Optional<SparkDto> getSparkBySlug(String slug) {
        return sparkRepository.findBySlug(slug)
                .map(SparkDto::fromEntity);
    }
}
