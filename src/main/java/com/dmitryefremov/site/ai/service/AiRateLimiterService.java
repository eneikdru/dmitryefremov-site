package com.dmitryefremov.site.ai.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AiRateLimiterService {

    private final Clock clock;
    private final int maxRequestsPerMinute;
    private final Map<String, RequestBucket> buckets = new ConcurrentHashMap<>();

    public AiRateLimiterService() {
        this(Clock.systemUTC(), 10);
    }

    @Autowired
    public AiRateLimiterService(Clock clock) {
        this(clock, 10);
    }

    public AiRateLimiterService(Clock clock, int maxRequestsPerMinute) {
        this.clock = clock != null ? clock : Clock.systemUTC();
        this.maxRequestsPerMinute = maxRequestsPerMinute;
    }

    public synchronized boolean isAllowed(String key) {
        long nowMillis = clock.millis();
        RequestBucket bucket = buckets.computeIfAbsent(key, k -> new RequestBucket(nowMillis, 0));

        if (nowMillis - bucket.windowStartMillis >= 60000) {
            bucket.windowStartMillis = nowMillis;
            bucket.requestCount = 0;
        }

        if (bucket.requestCount < maxRequestsPerMinute) {
            bucket.requestCount++;
            return true;
        }

        return false;
    }

    public void reset() {
        buckets.clear();
    }

    private static class RequestBucket {
        long windowStartMillis;
        int requestCount;

        RequestBucket(long windowStartMillis, int requestCount) {
            this.windowStartMillis = windowStartMillis;
            this.requestCount = requestCount;
        }
    }
}
