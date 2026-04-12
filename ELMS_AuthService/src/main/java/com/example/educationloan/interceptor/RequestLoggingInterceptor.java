package com.example.educationloan.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {
    private static final String START_TIME = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME, startTime);
        log.info("Pre-Handle <- Incoming Request | Method: {} | URL: {} | IP: {}", request.getMethod(), request.getRequestURI(), request.getRemoteAddr());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        log.info("Post-Handle -> Response Sent | URL: {} | Status: {}",request.getRequestURI(), response.getStatus());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        long startTime  = (Long) request.getAttribute(START_TIME);
        long timeTaken  = System.currentTimeMillis() - startTime;
        log.info("after-Completion - Request Complete  | URL: {} | Time: {}ms | Status: {}", request.getRequestURI(), timeTaken,response.getStatus());
        if (ex != null) {
            log.error("Exception occurred: {}", ex.getMessage());
        }
    }
}

