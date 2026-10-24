package com.dmitryefremov.site.contact.repository;

import com.dmitryefremov.site.contact.domain.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {

    List<ContactMessage> findBySenderEmail(String senderEmail);

    List<ContactMessage> findByStatus(String status);
}
