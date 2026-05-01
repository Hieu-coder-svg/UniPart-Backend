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
    USER_EXISTED(1011,"Tên người dùng đã tồn tại",HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1012,"Email đã tồn tại",HttpStatus.BAD_REQUEST),
    INVALID_OTP(1013,"Mã OTP không tồn tại hoặc đã được sử dụng",HttpStatus.BAD_REQUEST),
    INVALID_OTP_FORMAT(1014,"Mã OTP phải là định dạng số",HttpStatus.BAD_REQUEST),
    WRONG_OTP(1015,"Mã OTP không chính xác",HttpStatus.BAD_REQUEST),
    EXPIRED_OTP(1016,"Mã OTP đã hết hạn",HttpStatus.BAD_REQUEST),
    EXIST_PHONE(1017,"Số điện thoại đã tồn tại",HttpStatus.BAD_REQUEST),

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
    // Review
    JOB_NOT_FOUND(4001, "Job does not exist", HttpStatus.NOT_FOUND),
    STUDENT_NOT_FOUND(4002, "Student does not exist", HttpStatus.NOT_FOUND),
    APPLICATION_NOT_COMPLETED(4003, "Job has not been completed yet", HttpStatus.CONFLICT),
    REVIEW_ALREADY_EXISTS(4004, "You have already reviewed this job", HttpStatus.CONFLICT),
    REVIEW_RATING_REQUIRED(4005, "Rating is required", HttpStatus.BAD_REQUEST),
    REVIEW_RATING_INVALID(4006, "Rating must be between 1 and 5", HttpStatus.BAD_REQUEST),
    REVIEW_JOB_FORBIDDEN(4007, "You do not have permission to review this job", HttpStatus.FORBIDDEN),
    // Report
    REPORT_NOT_FOUND(5001, "Report does not exist", HttpStatus.NOT_FOUND),
    REPORT_ALREADY_EXISTS(5002, "You have already reported this target", HttpStatus.CONFLICT),
    REPORT_SELF_FORBIDDEN(5003, "You cannot report yourself", HttpStatus.BAD_REQUEST),
    REPORT_INVALID_TARGET_ID(5004, "Invalid target ID format", HttpStatus.BAD_REQUEST),
    REPORT_TARGET_NOT_FOUND(5005, "The reported target does not exist", HttpStatus.NOT_FOUND),
    // Community Post
    CATEGORY_NOT_FOUND(6001, "Category does not exist", HttpStatus.NOT_FOUND),
    CATEGORY_NAME_EXISTS(6002, "Category name already exists", HttpStatus.CONFLICT),
    CATEGORY_HAS_POSTS(6003, "Cannot delete category with existing posts", HttpStatus.CONFLICT),
    POST_NOT_FOUND(6004, "Post does not exist", HttpStatus.NOT_FOUND),
    POST_FORBIDDEN(6005, "You do not have permission to modify this post", HttpStatus.FORBIDDEN),
    COMMENT_NOT_FOUND(6006, "Comment does not exist", HttpStatus.NOT_FOUND),
    COMMENT_FORBIDDEN(6007, "You do not have permission to delete this comment", HttpStatus.FORBIDDEN),
    REPLY_DEPTH_EXCEEDED(6008, "Reply nesting is limited to one level", HttpStatus.BAD_REQUEST),
    ;
    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
