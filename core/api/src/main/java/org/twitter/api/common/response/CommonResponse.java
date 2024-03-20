package org.twitter.api.common.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.twitter.api.common.exception.ErrorMessage;

@Getter
@Builder
public class CommonResponse<T> {

    private final ResponseType resultType;
    private int statusCode;
    private T data;
    private String errorMessage;

    private CommonResponse(ResponseType resultType, int statusCode, T data, String errorMessage) {
        this.resultType = resultType;
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
        this.data = data;
    }

    public static <T> CommonResponse<T> success() {
        return new CommonResponse<>(
                ResponseType.SUCCESS,
                HttpStatus.OK.value(),
                null,
                null
        );
    }

    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(
                ResponseType.SUCCESS,
                HttpStatus.OK.value(),
                data,
                null
        );
    }

    public static <T> CommonResponse<T> success(int statusCode, T data) {
        return new CommonResponse<>(
                ResponseType.SUCCESS,
                statusCode,
                data,
                null
        );
    }

    public static <T extends HttpStatus> CommonResponse<T> fail(ErrorMessage message) {
        return new CommonResponse(
                ResponseType.ERROR,
                message.getStatus(),
                null,
                message.getErrorCode() + " : " + message.getMessage()
        );
    }

    public static <T extends HttpStatus> CommonResponse<T> fail(ErrorMessage message, T errorData) {
        return new CommonResponse(
                ResponseType.ERROR,
                message.getStatus(),
                errorData,
                message.getErrorCode() + " : " + message.getMessage()
        );
    }
}