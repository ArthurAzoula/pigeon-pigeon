package com.pigeonpigeon.pigeon.configuration.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDate;

@RestControllerAdvice
@ControllerAdvice
@Slf4j
public class ExceptionResponseHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(PigeonException.class)
    protected ResponseEntity<Object> handleApplicationException(PigeonException ex, WebRequest req) {
        log.warn("[Pigeon] PigeonException occurred: {}", ex.getMessage(), ex);
        var error = new ErrorDetails(LocalDate.now(), ex.getHttpStatus(), ex.getDetails());
        return handleExceptionInternal(ex, error, new HttpHeaders(), ex.getHttpStatus(), req);
    }

    @ExceptionHandler(MethodNotAllowedException.class)
    protected ResponseEntity<Object> handleMethodNotAllowedException(MethodNotAllowedException ex, WebRequest req) {
        log.warn("[Pigeon] MethodNotAllowedException occurred: {}", ex.getMessage(), ex);
        var error = new ErrorDetails(LocalDate.now(), HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage());
        return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.METHOD_NOT_ALLOWED, req);
    }

    @ExceptionHandler(BadRequestException.class)
    protected ResponseEntity<Object> handleBadRequestException(BadRequestException ex, WebRequest req) {
        log.warn("[Pigeon] BadRequestException occurred: {}", ex.getMessage(), ex);
        var error = new ErrorDetails(LocalDate.now(), HttpStatus.BAD_REQUEST, ex.getMessage());
        return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.BAD_REQUEST, req);
    }

    @ExceptionHandler(HttpClientErrorException.UnprocessableEntity.class)
    protected ResponseEntity<Object> handleUnprocessable(HttpClientErrorException.UnprocessableEntity ex, WebRequest req) {
        log.warn("[Pigeon] HttpClientErrorException.UnprocessableEntity occurred: {}", ex.getMessage(), ex);
        var error = new ErrorDetails(LocalDate.now(), HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY, req);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleGenericException(Exception ex, WebRequest req) {
        log.error("[Pigeon] Exception occurred: {}", ex.getMessage(), ex);
        var error = new ErrorDetails(LocalDate.now(), HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, req);
    }
}