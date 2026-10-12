package com.dmitryefremov.site.spark.repository;

import com.dmitryefremov.site.spark.domain.Spark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SparkRepository extends JpaRepository<Spark, Long> {

    Optional<Spark> findBySlug(String slug);

    Page<Spark> findByDomain(String domain, Pageable pageable);
}
