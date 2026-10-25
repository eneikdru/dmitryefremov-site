package com.dmitryefremov.site.contact.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoggingEmailAcknowledgmentService implements EmailAcknowledgmentService {

    private static final Logger log = LoggerFactory.getLogger(LoggingEmailAcknowledgmentService.class);

    @Override
    public void dispatchAcknowledgment(String senderName, String senderEmail, String subject, String content) {
        log.info("Dispatching email acknowledgment to: {}, sender: {}", senderEmail, senderName);
        // Dispatch logic simulated without in-memory state accumulation.
    }
}
