package com.unipart.unipart_backend.mapper;

import com.unipart.unipart_backend.dto.request.StudentRegistrationRequest;
import com.unipart.unipart_backend.dto.request.StudentUpdateRequest;
import com.unipart.unipart_backend.dto.response.UserResponse;
import com.unipart.unipart_backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isBlocked", constant = "false")
    @Mapping(target = "isActived", constant = "false")
    @Mapping(target = "student", ignore = true)      // Quan trọng
    @Mapping(target = "employer", ignore = true)
    User toUserEntity(StudentRegistrationRequest request);

    void updateUserFromRequest(StudentUpdateRequest request, @MappingTarget User user);
    @Mapping(source = "role.name", target = "roleName")
    UserResponse toUserResponse(User user);
    List<UserResponse> toUserResponseList(List<User> users);
}
