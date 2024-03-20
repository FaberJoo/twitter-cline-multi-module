package org.twitter.api.common.filter;

import static org.twitter.api.common.utils.HttpHeaderUtil.getClientIP;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@Component
public class HttpLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        if (isAsyncDispatch(request)) {
            filterChain.doFilter(request, response);
        } else {
            doFilterWrapped(new ContentCachingRequestWrapper(request),
                new ContentCachingResponseWrapper(response), filterChain);
        }
    }

    protected void doFilterWrapped(ContentCachingRequestWrapper request,
        ContentCachingResponseWrapper response,
        FilterChain filterChain) throws IOException, ServletException {

        if(!request.getRequestURI().contains("actuator/")) {
            try {
                filterChain.doFilter(request, response);
                logRequest(request);
            } finally {
                logResponse(response);
                response.copyBodyToResponse();
            }
        } else {
            filterChain.doFilter(request, response);
            response.copyBodyToResponse();
        }
    }

    private static void logRequest(ContentCachingRequestWrapper request) throws IOException {
        String queryString = request.getQueryString();
        log.info("Request : \n {} {} uri=[{}]\n content-type[{}]\n client-ip[{}]\n user-agent[{}]",
            request.getProtocol(),
            request.getMethod(),
            queryString == null ? request.getRequestURI() : request.getRequestURI() + "?" + queryString,
            request.getContentType(), getClientIP(request), request.getHeader("User-Agent"));
        logPayload("Request", request.getContentType(), request.getContentAsByteArray());
    }

    private static void logResponse(ContentCachingResponseWrapper response) throws IOException {
        logPayload("Response", response.getContentType(), response.getContentAsByteArray());
    }

    private static void logPayload(String prefix, String contentType, byte[] rowData)
        throws IOException {
        boolean visible = isVisible(
            MediaType.valueOf(contentType == null ? "application/json" : contentType));

        if (visible) {
            if (rowData.length > 0) {
                String contentString = new String(rowData);
                log.info("{}\n Payload: {}", prefix, contentString);
            }
        } else {
            log.info("{} Payload: Binary Content", prefix);
        }
    }

    private static boolean isVisible(MediaType mediaType) {
        final List<MediaType> VISIBLE_TYPES = Arrays.asList(
            MediaType.valueOf("text/*"),
            MediaType.APPLICATION_FORM_URLENCODED,
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_XML,
            MediaType.valueOf("application/*+json"),
            MediaType.valueOf("application/*+xml"),
            MediaType.MULTIPART_FORM_DATA
        );

        return VISIBLE_TYPES.stream()
            .anyMatch(visibleType -> visibleType.includes(mediaType));
    }
}
