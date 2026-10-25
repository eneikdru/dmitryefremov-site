package com.dmitryefremov.site.canon.repository;

import com.dmitryefremov.site.canon.domain.CanonCrossReference;
import com.dmitryefremov.site.canon.domain.CanonRelationshipType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("siteCanonCrossReferenceRepository")
public interface CanonCrossReferenceRepository extends JpaRepository<CanonCrossReference, Long> {

    List<CanonCrossReference> findBySourceId(Long sourceId);

    List<CanonCrossReference> findByTargetId(Long targetId);

    Optional<CanonCrossReference> findBySourceIdAndTargetIdAndRelationshipType(
            Long sourceId, Long targetId, CanonRelationshipType relationshipType);

    @org.springframework.data.jpa.repository.Query("SELECT c FROM CanonCrossReference c WHERE " +
           "(:sourceSlug IS NULL OR LOWER(c.source.slug) = LOWER(:sourceSlug)) AND " +
           "(:targetSlug IS NULL OR LOWER(c.target.slug) = LOWER(:targetSlug)) AND " +
           "(:relationshipType IS NULL OR LOWER(CAST(c.relationshipType AS string)) = LOWER(:relationshipType))")
    org.springframework.data.domain.Page<CanonCrossReference> searchLinks(
            @org.springframework.data.repository.query.Param("sourceSlug") String sourceSlug,
            @org.springframework.data.repository.query.Param("targetSlug") String targetSlug,
            @org.springframework.data.repository.query.Param("relationshipType") String relationshipType,
            org.springframework.data.domain.Pageable pageable);
}
