package com.dmitryefremov.site.essay.repository;

import com.dmitryefremov.site.essay.domain.Essay;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EssayRepository extends JpaRepository<Essay, Long> {

    Optional<Essay> findBySlug(String slug);

    Page<Essay> findAllByOrderByPublishedAtDesc(Pageable pageable);
}
