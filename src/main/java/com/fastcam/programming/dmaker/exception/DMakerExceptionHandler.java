package com.fastcam.programming.dmaker.exception;

import com.fastcam.programming.dmaker.dto.DMakerErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
@Slf4j
public class DMakerExceptionHandler {
    @ExceptionHandler
    public DMakerErrorResponse handleException(
            DMakerException e,
            HttpServletRequest request
    ) {
        log.error("errorCode: {}, url : {}, message: {}",
                e.getDMakerErrorCode(), request.getRequestURI(), e.getDetailMessage());
        return DMakerErrorResponse.builder()
                .errorCode(e.getDMakerErrorCode())
                .errorMessage(e.getDetailMessage())
                .build();
    }
    // create-developr에 post가 아닌 다른 메소드로 요청했을거나 파라미터를 빼먹었을때 발생
    // Validation을 했을때 문제가 발생하면 발생하는 에러.
    // 컨테이너 진입전의 에러발생을 공통적인 exception handleing
    /* 에러코드는 직접 만들어준 에러코드를 넣어줄 수 없으므로 제외
    좀 더 정제된 에러코드를 던져줘야 하기 때문에 CustomException을 사용한다.
     */

    @ExceptionHandler(value = {
            HttpRequestMethodNotSupportedException.class,
            MethodArgumentNotValidException.class})
    public DMakerErrorResponse handlerBadRequest(
            Exception e, HttpServletRequest request) {
        log.error("url : {}, message : {}", request.getRequestURI(), e.getMessage());
        return DMakerErrorResponse.builder()
                .errorCode(DMakerErrorCode.INVALID_REQUEST)
                .errorMessage(DMakerErrorCode.INVALID_REQUEST.getMessage())
                .build();
    }

    // 그래도 완벽하지 않은 경우가 있다. 사용하는 라이브러리가 어떤 익센션을 발생시킬수도 알 수 없으므로
    // 그냥 익셉션을 받아서 처리해주는 익센션을 처리해야 한다.

    @ExceptionHandler(Exception.class)
    public DMakerErrorResponse handleException(
            Exception e, HttpServletRequest request) {
        log.error("url : {}, message : {}", request.getRequestURI(), e.getMessage());
        return DMakerErrorResponse.builder()
                .errorCode(DMakerErrorCode.INTERNAL_SERVER_ERROR)
                .errorMessage(DMakerErrorCode.INTERNAL_SERVER_ERROR.getMessage())
                .build();
    }

}
