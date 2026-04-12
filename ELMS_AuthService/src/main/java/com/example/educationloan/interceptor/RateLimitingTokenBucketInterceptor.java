package com.example.educationloan.interceptor;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RateLimitingTokenBucketInterceptor implements HandlerInterceptor {


    @Value("${app.interceptor.rate-limiting.enabled:true}")
    private boolean enabled;

    @Value("${app.interceptor.rate-limiting.bucket-capacity:5}")
    private int bucketCapacity;

    @Value("${app.interceptor.rate-limiting.refill-tokens:5}")
    private int refillTokens;

    @Value("${app.interceptor.rate-limiting.refill-duration-minutes:1}")
    private int refillDurationMinutes;

    @Value("${app.interceptor.rate-limiting.login-bucket-capacity:5}")
    private int loginBucketCapacity;

    @Value("${app.interceptor.rate-limiting.register-bucket-capacity:3}")
    private int registerBucketCapacity;

    @Value("${app.interceptor.rate-limiting.get-user-bucket-capacity:5}")
    private int getUserBucketCapacity;

    private record BucketEntry(Bucket bucket, Instant lastUsed) {}
    private final Map<String, BucketEntry> buckets = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        log.info("=================================================================================================");
        log.info("RateLimitingInterceptor for Token Bucket is ready.................................................");
        log.info("enabled               = {}", enabled);
        log.info("general capacity      = {}/min", bucketCapacity);
        log.info("login capacity        = {}/min", loginBucketCapacity);
        log.info("register capacity     = {}/min", registerBucketCapacity);
        log.info("get-user capacity     = {}/min", getUserBucketCapacity);
        log.info("=================================================================================================");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        if (!enabled) {
            return true;
        }

        String clientIp = getClientIp(request);
        String uri      = request.getRequestURI();
        Bucket bucket   = resolveBucket(clientIp, uri);

        if (bucket.tryConsume(1)) {
            long remaining = bucket.getAvailableTokens();
            log.debug("Token Allowed | IP: {} | URL: {} | Tokens left: {}", clientIp, uri, remaining);
            response.setHeader("X-Rate-Limit-Remaining", String.valueOf(remaining));
            return true;
        }

        log.warn("Token-Rate limit exceeded | IP: {} | URL: {}", clientIp, uri);
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("X-Rate-Limit-Remaining",   "0");
        response.setHeader("X-Rate-Limit-Retry-After", refillDurationMinutes + " minute(s)");
        response.getWriter().write("{\"success\":false," +
                "\"message\":\"Too many requests. Try again after " + refillDurationMinutes +
                " minute(s).\"," + "\"data\":null}");
        return false;
    }

    private Bucket resolveBucket(String clientIp, String uri) {

        if (uri.matches("/api/v1/users/.*")) {
            int capacity = getUserBucketCapacity;
            return getOrCreate("get-user:" + clientIp, capacity, capacity);
        }
        if (uri.contains("/api/v1/auth/login")) {
            int capacity = loginBucketCapacity;
            return getOrCreate("login:" + clientIp, capacity, capacity);
        }
        if (uri.contains("/api/v1/auth/register")) {
            int capacity = registerBucketCapacity;
            return getOrCreate("register:" + clientIp, capacity, capacity);
        }

        return getOrCreate(clientIp, bucketCapacity, refillTokens);
    }


    private Bucket getOrCreate(String key, int capacity, int refill) {
        BucketEntry entry = buckets.computeIfAbsent(key, k -> new BucketEntry(createBucket(capacity, refill), Instant.now()));
        buckets.put(key, new BucketEntry(entry.bucket(), Instant.now()));
        return entry.bucket();
    }

    private Bucket createBucket(int capacity, int refill) {
        Bandwidth limit = Bandwidth.classic(capacity, Refill.greedy(refill, Duration.ofMinutes(refillDurationMinutes)));
        return Bucket.builder().addLimit(limit).build();
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = request.getRemoteAddr();
        return ip;
    }


}