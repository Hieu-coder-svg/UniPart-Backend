package com.unipart.unipart_backend.service;

import com.unipart.unipart_backend.dto.request.StudentRegistrationRequest;
import com.unipart.unipart_backend.dto.response.UserResponse;
import com.unipart.unipart_backend.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface UserService  {
    UserResponse createStudentUser(StudentRegistrationRequest request);
    UserResponse updateStudentUser(String id,StudentRegistrationRequest request);
    List<UserResponse> getAll();
    UserResponse findUser(String id);
    UserResponse getMyInfo();
}
