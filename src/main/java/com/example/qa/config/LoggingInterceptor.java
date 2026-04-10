package com.example.qa.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        String uri = request.getRequestURI();

        if (uri.startsWith("/api/") && !uri.contains("/debug")) {
            long startTime = System.currentTimeMillis();
            request.setAttribute("startTime", startTime);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) throws Exception {
        String uri = request.getRequestURI();
        Long startTime = (Long) request.getAttribute("startTime");

        if (startTime != null && uri.startsWith("/api/")) {
            long duration = System.currentTimeMillis() - startTime;
            if (duration > 500 || response.getStatus() >= 400) {
                logger.info("{} {} - {}ms - status:{}", request.getMethod(), uri, duration, response.getStatus());
            }
        }
    }
}
