package com.unipart.unipart_backend.exception;

import com.unipart.unipart_backend.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.text.ParseException;
import java.util.Map;
import java.util.Objects;

@Slf4j
@ControllerAdvice
public class GlobalHandleException {

    private static final String MIN_ATTRIBUTES = "min";
    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
        log.error("Lỗi chưa xác định (Uncategorized Exception): ", exception);
        return ResponseEntity.badRequest().body(apiResponse);
    }
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatusCode())
                .body(apiResponse);
    }
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
       String enumKey = exception.getFieldError().getDefaultMessage();

        ErrorCode errorCode = ErrorCode.INVALID_KEY;
    Map<String,Object> attributes = null ;
    try{
        errorCode = ErrorCode.valueOf(enumKey);
        var constraintViolation = exception.getBindingResult()
                .getAllErrors().get(0).unwrap(ConstraintViolation.class);

        attributes = constraintViolation.getConstraintDescriptor().getAttributes();
        log.info(attributes.toString());

    }catch (IllegalArgumentException e){
        log.error(enumKey,e);
    }
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(Objects.nonNull(attributes) ?
                mapAttribute(errorCode.getMessage(),attributes)
                : errorCode.getMessage());

        return  ResponseEntity.badRequest().body(apiResponse);
    }
    @ExceptionHandler(value = ParseException.class)
    ResponseEntity<ApiResponse> handlingParseException(ParseException exception) {
        ErrorCode errorCode = ErrorCode.INVALID_JWT;
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }
    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse> handlingAccessDeniedException(AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZE;
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }
    private String mapAttribute(String message, Map<String,Object> map) {
        String minValue = map.get(MIN_ATTRIBUTES).toString();
        return message.replace("{"+MIN_ATTRIBUTES+"}", minValue);
    }
}
