package com.unipart.unipart_backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999,"Uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_EXISTS(1001, "Người dùng đã tồn tại",HttpStatus.BAD_REQUEST),
    EMAIL_EXIST(1002,"Email đã tồn tại",HttpStatus.BAD_REQUEST),
    USER_INVALID(1003,"Username phải từ 4 đến 50 ký tự",HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(1003,"Email không hợp lệ",HttpStatus.BAD_REQUEST),
    USER_NOT_EXIST(1004,"Username không tồn tại",HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1005,"Unauthenticated",HttpStatus.UNAUTHORIZED),
    INVALID_JWT(1006,"Invalid JWT",HttpStatus.BAD_REQUEST),
    INVALID_KEY(1007,"Invalid message key",HttpStatus.BAD_REQUEST),
    UNAUTHORIZE(1008,"Bạn không có quyền truy cập",HttpStatus.FORBIDDEN),
    INVALID_DOB(1009,"Bạn phải lớn hơn {min} tuổi",HttpStatus.FORBIDDEN),
    REMOVE_APPLICATION(1010,"Bạn không thể xóa",HttpStatus.BAD_REQUEST),

    // Application
    APPLICATION_NOT_FOUND(2001, "The application does not exist", HttpStatus.NOT_FOUND),
    APPLICATION_ALREADY_PROCESSED(2002, "The application has already been processed", HttpStatus.CONFLICT),
    JOB_IS_FULL(2003, "The job is full", HttpStatus.CONFLICT),
    APPLICATION_FORBIDDEN(2004, "You do not have permission to work with this application", HttpStatus.FORBIDDEN),
    // Package
    PACKAGE_NOT_FOUND(3001, "Package does not exist", HttpStatus.NOT_FOUND),
    EMPLOYER_NOT_FOUND(3002, "Employer does not exist", HttpStatus.NOT_FOUND),
    PURCHASE_NOT_FOUND(3003, "Purchase record not found", HttpStatus.NOT_FOUND),
    PAYMENT_INVALID_SIGNATURE(3004, "Invalid payment signature", HttpStatus.BAD_REQUEST),
    ;
    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
