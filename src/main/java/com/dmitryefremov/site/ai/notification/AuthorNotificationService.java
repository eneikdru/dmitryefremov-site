package com.dmitryefremov.site.ai.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service("siteAuthorNotificationService")
public class AuthorNotificationService {

    private final String authorEmail;
    private final Clock clock;
    private final List<AuthorNotification> dispatchedNotifications = Collections.synchronizedList(new ArrayList<>());

    @Autowired
    public AuthorNotificationService(@Value("${site.author.email:author@dmitryefremov.com}") String authorEmail,
                                     @Autowired(required = false) Clock clock) {
        this.authorEmail = authorEmail;
        this.clock = clock != null ? clock : Clock.systemUTC();
    }

    public AuthorNotificationService(String authorEmail) {
        this(authorEmail, Clock.systemUTC());
    }

    public AuthorNotification dispatchNotification(String submissionType,
                                                    String senderName,
                                                    String senderEmail,
                                                    String agentIdentifier,
                                                    String content) {
        OffsetDateTime now = OffsetDateTime.now(clock);
        AuthorNotification notification = new AuthorNotification(
                authorEmail,
                submissionType,
                senderName,
                senderEmail,
                agentIdentifier,
                content,
                now
        );
        dispatchedNotifications.add(notification);
        return notification;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public Clock getClock() {
        return clock;
    }

    public List<AuthorNotification> getDispatchedNotifications() {
        return new ArrayList<>(dispatchedNotifications);
    }

    public void clearDispatchedNotifications() {
        dispatchedNotifications.clear();
    }
}
