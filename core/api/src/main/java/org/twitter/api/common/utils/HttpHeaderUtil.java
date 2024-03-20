package org.twitter.api.common.utils;

import jakarta.servlet.http.HttpServletRequest;

public class HttpHeaderUtil {
    public static String getClientIP(HttpServletRequest request) {
        String clientIP = request.getHeader("X-Forwarded-For");

        if (clientIP == null || clientIP.isEmpty() || "unknown".equalsIgnoreCase(clientIP)) {
            clientIP = request.getHeader("X-Real-IP");
        }

        if (clientIP == null || clientIP.isEmpty() || "unknown".equalsIgnoreCase(clientIP)) {
            clientIP = request.getRemoteAddr();
        }

        return clientIP;
    }
}
