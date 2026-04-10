package com.unipart.unipart_backend.controller;

import com.unipart.unipart_backend.dto.request.StudentRegistrationRequest;
import com.unipart.unipart_backend.dto.response.ApiResponse;
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

    @PostMapping
    ApiResponse<UserResponse> createUser(@RequestBody StudentRegistrationRequest user){
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.createStudentUser(user));
        return apiResponse;

    }
    @GetMapping
    ApiResponse<List<UserResponse>>  getAllUsers(){

        return ApiResponse.<List<UserResponse>> builder()
                .result(userService.getAll())
                .build() ;
    }
    @PutMapping("/{id}")
    ApiResponse<UserResponse> updateUser(@PathVariable String id,@RequestBody StudentRegistrationRequest user){
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.updateStudentUser(id, user));
        return apiResponse;
    }
    @GetMapping("/{id}")
    ApiResponse<UserResponse> getUserId(@PathVariable String id){

        return ApiResponse.<UserResponse> builder()
                .result(userService.findUser(id))
                .build() ;
    }
    @GetMapping("/{myInfo}")
    ApiResponse<UserResponse> getMyInfo(@PathVariable String id){
        return ApiResponse.<UserResponse> builder()
                .result(userService.getMyInfo())
                .build() ;
    }
}
