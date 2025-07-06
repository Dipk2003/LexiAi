package com.lexiai.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitConfig {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    @Bean
    public Map<String, Bucket> rateLimitCache() {
        return cache;
    }

    public Bucket createNewBucket() {
        return Bucket.builder()
            .addLimit(Bandwidth.classic(50, Refill.intervally(50, Duration.ofMinutes(1))))
            .build();
    }

    public Bucket resolveBucket(String key) {
        return cache.computeIfAbsent(key, k -> createNewBucket());
    }
}
