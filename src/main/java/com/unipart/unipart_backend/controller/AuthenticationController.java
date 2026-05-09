package com.unipart.unipart_backend.controller;

import com.nimbusds.jose.JOSEException;
import com.unipart.unipart_backend.dto.request.AuthenticationRequest;
import com.unipart.unipart_backend.dto.request.ChangePasswordRequest;
import com.unipart.unipart_backend.dto.request.EmployerRegistrationRequest;
import com.unipart.unipart_backend.dto.request.ForgotPasswordRequest;
import com.unipart.unipart_backend.dto.request.IntrospectRequest;
import com.unipart.unipart_backend.dto.request.LogoutRequest;
import com.unipart.unipart_backend.dto.request.RefreshRequest;
import com.unipart.unipart_backend.dto.request.StudentRegistrationRequest;
import com.unipart.unipart_backend.dto.response.ApiResponse;
import com.unipart.unipart_backend.dto.response.AuthenticationResponse;
import com.unipart.unipart_backend.dto.response.EmployerResponse;
import com.unipart.unipart_backend.dto.response.IntrospectResponse;
import com.unipart.unipart_backend.dto.response.StudentResponse;
import com.unipart.unipart_backend.dto.response.UserResponse;
import com.unipart.unipart_backend.service.AuthenticationService;
import com.unipart.unipart_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    UserService userService;
    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest){
        var result = authenticationService.authenticate(authenticationRequest);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }
    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }
    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody RefreshRequest request) throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }
    @PostMapping("/logout")
    ApiResponse<Void> authenticateLogout(@RequestBody LogoutRequest request) throws ParseException, JOSEException{
       authenticationService.logout(request);
        return ApiResponse.<Void>builder()
                .build();
    }
    @PostMapping("/forgotPassword")
    ApiResponse<String> forgotPassword(@RequestBody ForgotPasswordRequest request){
        userService.forgotPassword(request);
        return ApiResponse.<String>builder()
                .result("send request")
                .build();
    }
    @PostMapping("/changePassword")
    ApiResponse<UserResponse> changePassword(@RequestBody ChangePasswordRequest request){
        return ApiResponse.<UserResponse>builder()
                .result(userService.changePassword(request))
                .build();
    }
    @PostMapping("/register-student")
    ApiResponse<StudentResponse> registerStudent(@RequestBody StudentRegistrationRequest user){
        ApiResponse<StudentResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.registerStudent(user));
        return apiResponse;

    }
    @PostMapping("/register-employer")
    ApiResponse<EmployerResponse> registerEmployer(@RequestBody EmployerRegistrationRequest user){
        ApiResponse<EmployerResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.registerEmployer(user));
        return apiResponse;

    }
    @GetMapping("/get-email")
    ApiResponse<String> getEmailByUsername(@RequestParam String username) {
        return ApiResponse.<String>builder()
                .result(userService.getEmailByUsername(username))
                .build();
    }

    @GetMapping("/test")
    public String test() {
        return "OK";
    }
}
