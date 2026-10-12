package com.dmitryefremov.site.canon.repository;

import com.dmitryefremov.site.canon.domain.CanonDomain;
import com.dmitryefremov.site.canon.domain.CanonItem;
import com.dmitryefremov.site.canon.domain.CanonType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("siteCanonItemRepository")
public interface CanonItemRepository extends JpaRepository<CanonItem, Long> {

    Optional<CanonItem> findBySlug(String slug);

    Page<CanonItem> findByDomainOrderByPublishedAtDesc(CanonDomain domain, Pageable pageable);

    Page<CanonItem> findByTypeOrderByPublishedAtDesc(CanonType type, Pageable pageable);

    Page<CanonItem> findByDomainAndTypeOrderByPublishedAtDesc(CanonDomain domain, CanonType type, Pageable pageable);

    Page<CanonItem> findAllByOrderByPublishedAtDesc(Pageable pageable);
}
