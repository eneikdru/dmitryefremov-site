package com.dmitryefremov.site.compliance.repository;

import com.dmitryefremov.site.compliance.domain.ServiceOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {

    List<ServiceOrder> findByUserId(String userId);

    long deleteByUserId(String userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE ServiceOrder s SET s.status = :newStatus WHERE s.id = :id AND s.status = :expectedStatus")
    int updateStatusAtomically(@Param("id") Long id, @Param("expectedStatus") String expectedStatus, @Param("newStatus") String newStatus);
}
