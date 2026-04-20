package com.unipart.unipart_backend.service.ServiceImpl;

import com.unipart.unipart_backend.dto.request.StudentRegistrationRequest;
import com.unipart.unipart_backend.dto.request.UserUpdateRequest;
import com.unipart.unipart_backend.dto.response.UserResponse;
import com.unipart.unipart_backend.entity.Role;
import com.unipart.unipart_backend.entity.User;
import com.unipart.unipart_backend.exception.AppException;
import com.unipart.unipart_backend.exception.ErrorCode;
import com.unipart.unipart_backend.mapper.UserMapper;
import com.unipart.unipart_backend.repository.UserRepository;
import com.unipart.unipart_backend.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal=true)
@Service
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    public UserResponse createStudentUser(StudentRegistrationRequest request) {

        if(userRepository.existsByUsername(request.getUsername())){
            throw new AppException(ErrorCode.USER_EXISTS);
        }
       User u =  userMapper.toUser(request);
        u.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        Role role = new Role();
        role.setName("STUDENT");
        u.setRole(role);
        return userMapper.toUserResponse(userRepository.save(u));
    }
    public UserResponse updateStudentUser(String id, UserUpdateRequest request) {

        User u = userRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXIST));
        userMapper.updateUser(u,request);

        return userMapper.toUserResponse(userRepository.save(u));
    }
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAll(){

        return userMapper.toUserResponseList(userRepository.findAll());
    }
    @PreAuthorize("returnObject.username == authentication.name")
    public UserResponse findUser(String id){
        User u = userRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXIST));
        return userMapper.toUserResponse(u);
    }

    public UserResponse getMyInfo(){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

       User byUserName =  userRepository.findByUsername(name)
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXIST));
        return userMapper.toUserResponse(byUserName);
    }
}
