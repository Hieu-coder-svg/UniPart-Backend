package com.unipart.unipart_backend.mapper;

import com.unipart.unipart_backend.dto.request.StudentRegistrationRequest;
import com.unipart.unipart_backend.dto.request.UserRegistrationRequest;
import com.unipart.unipart_backend.dto.request.UserUpdateRequest;
import com.unipart.unipart_backend.dto.response.UserResponse;
import com.unipart.unipart_backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toUserResponse(User user);
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
    User toUser(StudentRegistrationRequest request);
}
