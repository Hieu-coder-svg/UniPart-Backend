package com.unipart.unipart_backend.controller;

import com.unipart.unipart_backend.dto.request.ChangePasswordRequest;
import com.unipart.unipart_backend.dto.request.EmployerRegistrationRequest;
import com.unipart.unipart_backend.dto.request.EmployerUpdateRequest;
import com.unipart.unipart_backend.dto.request.ForgotPasswordRequest;
import com.unipart.unipart_backend.dto.request.StudentRegistrationRequest;
import com.unipart.unipart_backend.dto.request.StudentUpdateRequest;
import com.unipart.unipart_backend.dto.response.ApiResponse;
import com.unipart.unipart_backend.dto.response.EmployerResponse;
import com.unipart.unipart_backend.dto.response.StudentResponse;
import com.unipart.unipart_backend.dto.response.UserResponse;
import com.unipart.unipart_backend.entity.User;
import com.unipart.unipart_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    ApiResponse<List<UserResponse>>  getAllUsers(){
        return ApiResponse.<List<UserResponse>> builder()
                .result(userService.getAll())
                .build() ;
    }
    @PostMapping("/myStudentInfo")
    ApiResponse<StudentResponse> updateProfileStudent(@RequestBody StudentUpdateRequest request){
        ApiResponse<StudentResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.updateProfileStudent(request));
        return apiResponse;
    }

    @GetMapping("/myStudentInfo")
    ApiResponse<StudentResponse> getMyStudentInfo(){
        return ApiResponse.<StudentResponse> builder()
                .result(userService.getStudentMyInfo())
                .build() ;
    }
    @PostMapping("/myEmployerInfo")
    ApiResponse<EmployerResponse> updateProfileEmployer(@RequestBody EmployerUpdateRequest request){
        ApiResponse<EmployerResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.updateProfileEmployer(request));
        return apiResponse;
    }

    @GetMapping("/myEmployerInfo")
    ApiResponse<EmployerResponse> getMyEmployerInfo(){
        return ApiResponse.<EmployerResponse> builder()
                .result(userService.getEmployerMyInfo())
                .build() ;
    }
    @GetMapping("/{id}")
    ApiResponse<UserResponse> getUserId(@PathVariable String id){
        return ApiResponse.<UserResponse> builder()
                .result(userService.findUser(id))
                .build() ;
    }

    @PutMapping("/{id}/block")
    ApiResponse<Void> blockUser(@PathVariable String id) {
        userService.blockUser(id);
        return ApiResponse.<Void>builder().build();
    }

    @PutMapping("/{id}/unblock")
    ApiResponse<Void> unblockUser(@PathVariable String id) {
        userService.unblockUser(id);
        return ApiResponse.<Void>builder().build();
    }
}
