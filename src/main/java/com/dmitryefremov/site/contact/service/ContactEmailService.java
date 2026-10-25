package com.dmitryefremov.site.contact.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ContactEmailService {

    private static final Logger logger = LoggerFactory.getLogger(ContactEmailService.class);

    private final Clock clock;
    private final List<DispatchedEmail> dispatchedEmails = Collections.synchronizedList(new ArrayList<>());

    @Autowired
    public ContactEmailService(@Autowired(required = false) Clock clock) {
        this.clock = clock != null ? clock : Clock.systemUTC();
    }

    public ContactEmailService() {
        this(Clock.systemUTC());
    }

    public boolean dispatchAcknowledgement(String recipientEmail, String recipientName, String originalSubject, String originalContent) {
        String subject = (originalSubject != null && !originalSubject.isBlank())
                ? "Acknowledgement: " + originalSubject
                : "Thank you for contacting Dmitry Efremov";

        String body = String.format(
                "Hello %s,%n%nThank you for getting in touch. Your message has been received successfully and recorded.%n%nMessage Details:%n- Subject: %s%n- Received at: %s%n%nBest regards,%nDmitry Efremov",
                recipientName,
                originalSubject != null ? originalSubject : "(No Subject)",
                OffsetDateTime.now(clock)
        );

        DispatchedEmail email = new DispatchedEmail(
                recipientEmail,
                recipientName,
                subject,
                body,
                OffsetDateTime.now(clock)
        );

        dispatchedEmails.add(email);
        logger.info("Dispatched email acknowledgement to {} for recipient {}", recipientEmail, recipientName);

        return true;
    }

    public Clock getClock() {
        return clock;
    }

    public List<DispatchedEmail> getDispatchedEmails() {
        return new ArrayList<>(dispatchedEmails);
    }

    public void clearDispatchedEmails() {
        dispatchedEmails.clear();
    }

    public static class DispatchedEmail {
        private final String recipientEmail;
        private final String recipientName;
        private final String subject;
        private final String body;
        private final OffsetDateTime dispatchedAt;

        public DispatchedEmail(String recipientEmail, String recipientName, String subject, String body, OffsetDateTime dispatchedAt) {
            this.recipientEmail = recipientEmail;
            this.recipientName = recipientName;
            this.subject = subject;
            this.body = body;
            this.dispatchedAt = dispatchedAt;
        }

        public String getRecipientEmail() {
            return recipientEmail;
        }

        public String getRecipientName() {
            return recipientName;
        }

        public String getSubject() {
            return subject;
        }

        public String getBody() {
            return body;
        }

        public OffsetDateTime getDispatchedAt() {
            return dispatchedAt;
        }
    }
}
