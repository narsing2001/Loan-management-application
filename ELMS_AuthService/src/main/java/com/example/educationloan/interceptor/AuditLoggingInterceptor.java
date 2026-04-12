
package com.example.educationloan.interceptor;
import com.example.educationloan.config.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditLoggingInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${app.interceptor.audit-logging.enabled:true}")
    private boolean enabled;

    @Value("${app.interceptor.audit-logging.log-anonymous:true}")
    private boolean logAnonymous;

    @Value("${app.interceptor.audit-logging.exclude-urls:/actuator/health,/actuator/info,/favicon.ico}")
    private List<String> excludeUrls;

    private static final String START_TIME = "auditStartTime";

    @Override
    public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler) {
        if (!enabled) {
            return true;
        }
        request.setAttribute(START_TIME, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,HttpServletResponse response,Object handler,Exception ex) {
        if (!enabled) {
            return;
        }
        String uri = request.getRequestURI();

        for (String excludeUrl : excludeUrls) {
            if (uri.startsWith(excludeUrl)) {
                return;
            }
        }
        String username = extractUsername(request);
        if (!logAnonymous && "anonymous".equals(username)) {
            return;
        }
        long startTime = (Long) request.getAttribute(START_TIME);
        long timeTaken = System.currentTimeMillis() - startTime;
        String clientIp = getClientIp(request);
        log.info("[AUDIT] user={} | {} {} | IP={} | status={} | time={}ms", username,request.getMethod(), uri, clientIp, response.getStatus(), timeTaken);

        if (ex != null) {
            log.error("[AUDIT] Exception | user={} | {} {} | error={}", username, request.getMethod(), uri, ex.getMessage());
        }
    }

    private String extractUsername(HttpServletRequest request) {
        try {
            String bearer = request.getHeader("Authorization");
            if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
                return jwtTokenProvider.extractUsername(bearer.substring(7));
            }
        } catch (Exception ignored) {}
        return "anonymous";
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = request.getRemoteAddr();
        return ip;
    }
}

