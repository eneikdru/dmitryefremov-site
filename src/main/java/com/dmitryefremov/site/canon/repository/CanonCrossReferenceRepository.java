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
}
