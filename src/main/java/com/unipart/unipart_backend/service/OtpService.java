package com.unipart.unipart_backend.service;

import com.unipart.unipart_backend.dto.request.SendOTPRequest;
import com.unipart.unipart_backend.dto.request.VerifyOTPRequest;

public interface OtpService {
    void generateAndSendOtp(SendOTPRequest request);
    void verifyOtp(VerifyOTPRequest request);
    void validateAndUseOtp(String email, String otp);
}