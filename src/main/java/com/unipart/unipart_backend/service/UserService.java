package com.unipart.unipart_backend.service;

import com.unipart.unipart_backend.dto.request.ChangePasswordRequest;
import com.unipart.unipart_backend.dto.request.EmployerRegistrationRequest;
import com.unipart.unipart_backend.dto.request.EmployerUpdateRequest;
import com.unipart.unipart_backend.dto.request.ForgotPasswordRequest;
import com.unipart.unipart_backend.dto.request.StudentRegistrationRequest;
import com.unipart.unipart_backend.dto.request.StudentUpdateRequest;
import com.unipart.unipart_backend.dto.request.UserUpdateRequest;
import com.unipart.unipart_backend.dto.response.EmployerResponse;
import com.unipart.unipart_backend.dto.response.StudentResponse;
import com.unipart.unipart_backend.dto.response.UserResponse;
import com.unipart.unipart_backend.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
public interface UserService  {
    StudentResponse registerStudent(StudentRegistrationRequest request);
    List<UserResponse> getAll();
    UserResponse findUser(String id);
    StudentResponse getStudentMyInfo();
    StudentResponse updateProfileStudent(StudentUpdateRequest request);
    EmployerResponse registerEmployer(EmployerRegistrationRequest request);
    EmployerResponse updateProfileEmployer(EmployerUpdateRequest request);
    EmployerResponse getEmployerMyInfo();
    void forgotPassword(ForgotPasswordRequest request);
    UserResponse changePassword(ChangePasswordRequest request);
    void blockUser(String id);
    void unblockUser(String id);
    String getEmailByUsername(String username);
}
