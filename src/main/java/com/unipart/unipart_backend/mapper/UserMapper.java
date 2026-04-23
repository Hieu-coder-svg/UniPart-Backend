package com.unipart.unipart_backend.mapper;

import com.unipart.unipart_backend.dto.request.StudentRegistrationRequest;
import com.unipart.unipart_backend.dto.request.StudentUpdateRequest;
import com.unipart.unipart_backend.dto.request.UserUpdateRequest;
import com.unipart.unipart_backend.dto.response.StudentResponse;
import com.unipart.unipart_backend.dto.response.UserResponse;
import com.unipart.unipart_backend.entity.Student;
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

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "fullName", source = "user.fullName")
    @Mapping(target = "dateOfBirth", source = "user.dateOfBirth")
    @Mapping(target = "phoneNumber", source = "user.phoneNumber")
    @Mapping(target = "gender", source = "user.gender")
    @Mapping(target = "isBlocked", source = "user.isBlocked")
    @Mapping(target = "isActived", source = "user.isActived")
    @Mapping(target = "roleName", source = "user.role.name")
    @Mapping(target = "createdAt", source = "user.createdAt")
    @Mapping(target = "updatedAt", source = "user.updatedAt")

    @Mapping(target = "university", source = "user.student.university")
    @Mapping(target = "major", source = "user.student.major")
    @Mapping(target = "address", source = "user.student.address")
    @Mapping(target = "latitude", source = "user.student.latitude")
    @Mapping(target = "longitude", source = "user.student.longitude")
    StudentResponse toStudentResponse(User user);

    void updateUserFromRequest(StudentUpdateRequest request, @MappingTarget User user);
    UserResponse toUserResponse(User user);
    List<UserResponse> toUserResponseList(List<User> users);
}
