package org.twitter.api.common.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ErrorMessage {
    private final int status;
    private final String errorCode;
    private final String message;
    private final Object data;

    private ErrorMessage(int status, String errorCode, String message, Object data) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
        this.data = data;
    }

    public static ErrorMessage of(final ErrorCode errorCode) {
        return new ErrorMessage(
                errorCode.getStatus(),
                errorCode.getCode(),
                errorCode.getMessage(),
                null
        );
    }

    public static ErrorMessage of(final ErrorCode errorCode, final String message) {
        return new ErrorMessage(
                errorCode.getStatus(),
                errorCode.getCode(),
                message,
                null
        );
    }

    public static ErrorMessage of(final ErrorCode errorCode, final String message, final Object data) {
        return new ErrorMessage(
                errorCode.getStatus(),
                errorCode.getCode(),
                errorCode.getMessage(),
                data instanceof BindingResult ? FieldError.of(data) : data
        );
    }

    public static ErrorMessage of(final ConstraintViolationException e) {
        final Object errors =
                e.getConstraintViolations().stream().map(violation -> {
                    String fullPath = violation.getPropertyPath().toString();
                    String fieldName = fullPath.substring(
                            fullPath.lastIndexOf('.') + 1); // 마지막 '.' 이후의 문자열을
                    // 필드 이름으로 추출
                    return new ErrorMessage.FieldError(fieldName,
                            violation.getInvalidValue() == null ? ""
                                    : violation.getInvalidValue().toString(),
                            violation.getMessage());
                }).collect(Collectors.toList());

        return ErrorMessage.of(ErrorCode.INVALID_INPUT_VALUE, null, errors);
    }

    public static ErrorMessage of(final MethodArgumentTypeMismatchException e) {
        final String value = e.getValue() == null ? "" : e.getValue().toString();
        final Object errors =
                ErrorMessage.FieldError.of(e.getName(), value, e.getErrorCode());
        return ErrorMessage.of(ErrorCode.INVALID_VALUE_TYPES, null, errors);
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class FieldError {

        private String field;
        private String value;
        private String reason;

        private FieldError(final String field, final String value, final String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }

        public static List<FieldError> of(final String field, final String value, final String reason) {
            List<FieldError> fieldErrors = new ArrayList<>();
            fieldErrors.add(new FieldError(field, value, reason));
            return fieldErrors;
        }

        private static List<FieldError> of(final Object bindingResult) {
            final List<org.springframework.validation.FieldError> fieldErrors =
                    ((BindingResult)bindingResult).getFieldErrors();
            return fieldErrors.stream().map(error -> {
                String reason = error.getDefaultMessage();

                // 타입 변환 실패 메시지
                if (reason.startsWith("Failed to convert")) {
                    reason = "Invalid format for field '" + error.getField();
                }
                return new FieldError(error.getField(),
                        error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                        reason);
            }).collect(Collectors.toList());
        }
    }
}
