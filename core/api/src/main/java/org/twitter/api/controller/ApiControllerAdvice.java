package org.twitter.api.controller;

import org.twitter.api.common.exception.BusinessException;
import org.twitter.api.common.exception.ErrorCode;
import org.twitter.api.common.exception.ErrorMessage;
import org.twitter.api.common.response.CommonResponse;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RequiredArgsConstructor
@Slf4j
@RestControllerAdvice
public class ApiControllerAdvice {

    /**
     * - 주로 @Valid 또는 @Validated 에서 발생한 요청 데이터 검증 실패 예외를 처리 <br>
     * - HttpMessageConverter에서 등록한 HttpMessageConverter binding 못 할경우 발생
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<CommonResponse> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException e) {
        final CommonResponse response = CommonResponse.fail(
                ErrorMessage.of(ErrorCode.INVALID_INPUT_VALUE, null, e.getBindingResult())
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * - javax.validation 을 이용한 파라미터 유효성 검사에서 실패할 경우 발생 <br>
     * - @Min(1) int id 에서 id가 1보다 작을 경우 발생
     */
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<CommonResponse> handleConstraintViolationException(
        ConstraintViolationException e) {
        final CommonResponse response = CommonResponse.fail(ErrorMessage.of(e));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * - 컨트롤러 핸들러 메소드의 매개변수에 바인딩 할 때 발생하는 예외를 처리 <br>
     * - @RequestParam 등에 존재하지 않는 값이 들어올 경우 발생
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<CommonResponse> handleMethodArgumentTypeMismatchException(
        MethodArgumentTypeMismatchException e) {
        final CommonResponse response = CommonResponse.fail(ErrorMessage.of(e));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * - 지원하지 않은 HTTP 메소드 요청 예외 처리
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<CommonResponse> handleHttpRequestMethodNotSupportedException(
        HttpRequestMethodNotSupportedException e) {
        final CommonResponse response = CommonResponse.fail(ErrorMessage.of(ErrorCode.METHOD_NOT_ALLOWED));
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * - 요청 본문 (HTTP BODY) 읽을 수 없는 경우 JSON 형식의 요청 본문을 객체로 변환할 수 없어 발생하는 예외를 처리
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<CommonResponse> handleHttpMessageNotReadableException(
        HttpMessageNotReadableException e) {
        final CommonResponse response = CommonResponse.fail(ErrorMessage.of(ErrorCode.BODY_NOT_READABLE));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * - 요청 시 필요한 쿼리 파라미터가 누락되었을 경우 발생하는 예외를 처리
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<CommonResponse> handleMissingServletRequestParameterException(
        MissingServletRequestParameterException e) {
        final CommonResponse response = CommonResponse.fail(ErrorMessage.of(ErrorCode.PARAMETER_REQUIRED));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * - 지원하지 않은 HTTP 미디어 타입 요청 예외 처리
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    protected ResponseEntity<CommonResponse> handleHttpMediaTypeNotSupportedException(
        HttpMediaTypeNotSupportedException e) {
        final CommonResponse response = CommonResponse.fail(ErrorMessage.of(ErrorCode.UNSUPPORTED_MEDIA_TYPE));
        return new ResponseEntity<>(response,
            HttpStatus.valueOf(ErrorCode.UNSUPPORTED_MEDIA_TYPE.getStatus()));
    }

    /**
     * - 존재하지 않는 리소스 요청 예외 처리
     */
    @ExceptionHandler(NoResourceFoundException.class)
    protected ResponseEntity<CommonResponse> handleNoResourceFoundException(
        NoResourceFoundException e) {
        final CommonResponse response = CommonResponse.fail(ErrorMessage.of(ErrorCode.NOT_FOUND));
        log.warn("no resource found exception");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    protected ResponseEntity<CommonResponse> handleNoHandlerFoundException(
        NoHandlerFoundException e) {
        final CommonResponse response = CommonResponse.fail(ErrorMessage.of(ErrorCode.NOT_FOUND));
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * - 파일 업로드 용량 초과 예외 처리
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    protected ResponseEntity<CommonResponse> handleMaxUploadSizeExceededException(
        MaxUploadSizeExceededException e) {
        final CommonResponse response = CommonResponse.fail(ErrorMessage.of(ErrorCode.PAYLOAD_TOO_LARGE));
        return new ResponseEntity<>(response,
            HttpStatus.valueOf(ErrorCode.PAYLOAD_TOO_LARGE.getStatus()));
    }

    /**
     * - 비즈니스 로직에서 발생하는 예외를 처리
     */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<CommonResponse> handleBusinessException(final BusinessException e) {
        final ErrorCode errorCode = e.getErrorCode();
        final CommonResponse response = CommonResponse.fail(ErrorMessage.of(errorCode));
        return new ResponseEntity<>(response, HttpStatus.valueOf(errorCode.getStatus()));
    }

    /**
     * - NullPointerException 예외 처리 <br>
     * - 예상치 못한 서버 예외는 운영에 치명적일 수 있으므로 반드시 로그를 기록 및 예외 처리를 해야함. <br>
     * - 관리자 알림 기능도 추가하는 것이 좋음.
     */
    protected ResponseEntity<CommonResponse> handleNullPointerException(final NullPointerException e) {
        final CommonResponse response = CommonResponse.fail(ErrorMessage.of(ErrorCode.INTERNAL_SERVER_ERROR));
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * - 위에서 지정한 예외 외의 서버 로직 예외에 대한 예외 처리. <br>
     * - 예상치 못한 서버 예외는 운영에 치명적일 수 있으므로 반드시 로그를 기록 및 예외 처리를 해야함. <br>
     * - 관리자 알림 기능도 추가하는 것이 좋음.
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<CommonResponse> handleException(Exception e) {
        System.out.println(e);
        final CommonResponse response = CommonResponse.fail(
                ErrorMessage.of(ErrorCode.INTERNAL_SERVER_ERROR));
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
