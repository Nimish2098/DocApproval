package com.Project.DocApproval.config.requestRateLimiter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bucket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class RateLimitConfig {

  @Bean
  public Cache<String, Bucket> rateLimitCache() {
    return Caffeine.newBuilder()
                   .maximumSize(10_000)
                   .expireAfterAccess(10, TimeUnit.MINUTES)
                   .build();
  }
}