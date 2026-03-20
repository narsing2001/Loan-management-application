
/*
package com.example.educationloan.interceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.beans.factory.annotation.Value;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class RateLimitingFixedWindowInterceptor implements HandlerInterceptor {

    @Value("${app.interceptor.rate-limiting.fixed-window.max-requests}")
    private int maxRequests;

    @Value("${app.interceptor.rate-limiting.fixed-window.time-window-ms}")
    private long timeWindowMs;

    @Value("${app.interceptor.rate-limiting.fixed-window.enabled}")
    private boolean enabled;

    private final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private final Map<String, Long> windowStartTime = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        if (!enabled) {
            return true;
        }
        String clientIp = getClientIp(request);
        long now = System.currentTimeMillis();
        windowStartTime.putIfAbsent(clientIp, now);
        requestCounts.putIfAbsent(clientIp, new AtomicInteger(0));
        long windowStart = windowStartTime.get(clientIp);

        if (now - windowStart > timeWindowMs) {
            windowStartTime.put(clientIp, now);
            requestCounts.put(clientIp, new AtomicInteger(0));
        }
        int currentCount = requestCounts.get(clientIp).incrementAndGet();
        if (currentCount > maxRequests) {
            log.warn("Fix-WindowRate limit exceeded for IP: {} | Count: {}", clientIp, currentCount);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":false," +
                                           "\"message\":\"Too many requests. Please try again later.\"," +
                                           "\"data\":null}");
            return false;
        }
        log.debug("Fix-Window Rate limit check | IP: {} | Count: {}/{}", clientIp, currentCount, maxRequests);
        return true;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
 */