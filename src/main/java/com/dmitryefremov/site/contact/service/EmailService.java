package com.dmitryefremov.site.contact.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    public void sendAcknowledgement(String toEmail, String name) {
        log.info("Sending acknowledgement email to {} for {}", toEmail, name);
        // This is a stub for the actual email sending logic, which could use Spring Mail or a third-party API.
        // It satisfies the stateless requirement.
    }
}
