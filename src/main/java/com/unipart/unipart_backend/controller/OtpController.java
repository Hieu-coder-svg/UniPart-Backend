package com.unipart.unipart_backend.controller;

import com.unipart.unipart_backend.dto.request.SendOTPRequest;
import com.unipart.unipart_backend.dto.request.VerifyOTPRequest;
import com.unipart.unipart_backend.dto.response.ApiResponse;
import com.unipart.unipart_backend.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class OtpController {
    @Autowired
    OtpService otpService;
    @PostMapping("/resetOtp")
    ApiResponse<String> resetOtp(@RequestBody SendOTPRequest otpRequest){
        otpService.generateAndSendOtp(otpRequest);
        return ApiResponse.<String>builder()
                .result("Send OTP to email")
                .build();
    }
    @PostMapping("/verifyOtp")
    ApiResponse<String> verifyOTP(@RequestBody VerifyOTPRequest request){
        otpService.verifyOtp(request);
        return ApiResponse.<String>builder()
                .result("success")
                .build();
    }
}
