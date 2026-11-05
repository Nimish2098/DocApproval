package com.Project.DocApproval.config.requestRateLimiter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;

import java.time.Duration;

@Service
public class RateLimitService {

  private final Cache<String, Bucket> rateLimitCache;
  public RateLimitService(Cache<String,Bucket> rateLimitCache){
    this.rateLimitCache =rateLimitCache;
  }
  private Bucket createBucket() {

    Bandwidth limit = Bandwidth.builder()
                               .capacity(3)
                               .refillGreedy(3, Duration.ofMinutes(1))
                               .build();

    return Bucket.builder()
                 .addLimit(limit)
                 .build();
  }

  public boolean allowRequest(String clientIp) {

    Bucket bucket = rateLimitCache.get(
        clientIp,
        key -> createBucket()
    );

    return bucket.tryConsume(1);
  }
}