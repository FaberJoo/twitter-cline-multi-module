package org.twitter.api.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // 400 Bad Request
    BAD_REQUEST(400,"BR000", "Bad Request"),
    INVALID_INPUT_VALUE(400,"BR001", "Invalid input value"),
    INVALID_VALUE_TYPES(400, "B002", "Invalid value types"),
    BODY_NOT_READABLE(400, "BR003", "Request body is not readable"),
    PARAMETER_REQUIRED(400, "BR004", "Parameter is required"),

    // 401 Unauthorized
    UNAUTHORIZED(401, "UA000", "Unauthorized access"),

    // 402 Payment Required
    PAYMENT_REQUIRED(402, "PR000", "Payment required"),

    // 403 Forbidden
    FORBIDDEN(403, "F000", "Forbidden"),

    // 404 Not Found
    NOT_FOUND(404, "NF000", "Resource not found"),

    // 405 Method Not Allowed
    METHOD_NOT_ALLOWED(405, "MNA000", "Invalid Method"),

    // 406 Not Acceptable
    NOT_ACCEPTABLE(406, "NA000", "Not Acceptable"),

    // 408 Request Timeout
    REQUEST_TIMEOUT(408, "RT000", "Request Timeout"),

    // 409 Conflict
    CONFLICT(409, "C000", "Conflict"),

    // 410 Gone
    GONE(410, "G000", "Gone"),

    // 411 Length Required
    LENGTH_REQUIRED(411, "LR000", "Length Required"),

    // 412 Precondition Failed
    PRECONDITION_FAILED(412, "PF000", "Precondition Failed"),

    // 413 Payload Too Large
    PAYLOAD_TOO_LARGE(413, "P000", "File size exceeds maximum limit"),

    // 415 Unsupported Media Type
    UNSUPPORTED_MEDIA_TYPE(415, "M000", "Media type not supported"),

    // 416 Requested Range Not Satisfiable
    MISDIRECTED_REQUEST(421, "MR000", "Misdirected Request"),

    // 422 Unprocessable Entity
    TOO_MANY_REQUESTS(429, "TMR000", "Too Many Requests"),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(500, "ISE000", "Internal Server Error"),

    // 502 Bad Gateway
    BAD_GATEWAY(502, "BG000", "Bad Gateway"),

    // 503 Service Unavailable
    SERVICE_UNAVAILABLE(503, "SU000", "Service Unavailable"),

    // 504 Gateway Timeout
    GATEWAY_TIMEOUT(504, "GT000", "Gateway Timeout");

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}