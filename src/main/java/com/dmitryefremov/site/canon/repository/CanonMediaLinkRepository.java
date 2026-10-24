package com.dmitryefremov.site.canon.repository;

import com.dmitryefremov.site.canon.domain.CanonMediaLink;
import com.dmitryefremov.site.canon.domain.MediaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("siteCanonMediaLinkRepository")
public interface CanonMediaLinkRepository extends JpaRepository<CanonMediaLink, Long> {

    List<CanonMediaLink> findByCanonItemId(Long canonItemId);

    List<CanonMediaLink> findByMediaTypeAndMediaId(MediaType mediaType, Long mediaId);

    Optional<CanonMediaLink> findByCanonItemIdAndMediaTypeAndMediaIdAndRelationshipType(
            Long canonItemId, MediaType mediaType, Long mediaId, String relationshipType);
}
