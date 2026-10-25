package com.dmitryefremov.site.admin.repository;

import com.dmitryefremov.site.admin.domain.ConsentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsentRecordRepository extends JpaRepository<ConsentRecord, Long> {
    List<ConsentRecord> findByUserId(String userId);

    long deleteByUserId(String userId);
}
