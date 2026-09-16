package com.example.base.exception;

import com.example.base.common.response.ApiResponse;
import com.example.base.constant.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(AppException.class)
  public ResponseEntity<ApiResponse<Void>> handleAppException(AppException ex) {
    log.warn("Application exception: {}", ex.getMessage());
    return ResponseEntity
            .status(ex.getStatus())
            .body(ApiResponse.error(ex.getStatus().value(), ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
    String message = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
    return ResponseEntity
            .badRequest()
            .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), message));
  }

  @ExceptionHandler(BindException.class)
  public ResponseEntity<ApiResponse<Void>> handleBindException(BindException ex) {
    String message = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
    return ResponseEntity
            .badRequest()
            .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), message));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
    return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), ErrorMessage.FORBIDDEN));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleInternalServerError(Exception ex) {
    log.error("Unexpected exception", ex);
    return ResponseEntity
            .internalServerError()
            .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ErrorMessage.ERR_EXCEPTION_GENERAL));
  }
}
