package com.dmitryefremov.site.contact.repository;

import com.dmitryefremov.site.contact.domain.ContactMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {

    List<ContactMessage> findBySenderEmail(String senderEmail);
    Page<ContactMessage> findBySenderEmail(String senderEmail, Pageable pageable);

    List<ContactMessage> findByStatus(String status);
    Page<ContactMessage> findByStatus(String status, Pageable pageable);
}
