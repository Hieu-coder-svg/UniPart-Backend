package com.unipart.unipart_backend.mapper;

import com.unipart.unipart_backend.dto.request.StudentRegistrationRequest;
import com.unipart.unipart_backend.dto.request.StudentUpdateRequest;
import com.unipart.unipart_backend.dto.response.StudentResponse;
import com.unipart.unipart_backend.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StudentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "rating", ignore = true)
    Student toStudentEntity(StudentRegistrationRequest request);
    
    void updateStudentFromRequest(StudentUpdateRequest request, @MappingTarget Student student);
    
    @Mapping(target = "id", source = "student.user.id")
    @Mapping(target = "username", source = "student.user.username")
    @Mapping(target = "email", source = "student.user.email")
    @Mapping(target = "fullName", source = "student.user.fullName")
    @Mapping(target = "dateOfBirth", source = "student.user.dateOfBirth")
    @Mapping(target = "phoneNumber", source = "student.user.phoneNumber")
    @Mapping(target = "gender", source = "student.user.gender")
    @Mapping(target = "isBlocked", source = "student.user.isBlocked")
    @Mapping(target = "isActived", source = "student.user.isActived")
    @Mapping(target = "roleName", source = "student.user.role.name")
    @Mapping(target = "createdAt", source = "student.user.createdAt")
    @Mapping(target = "updatedAt", source = "student.user.updatedAt")
    @Mapping(target = "avatar", source = "student.user.avatar")
    @Mapping(target = "university", source = "student.university")
    @Mapping(target = "major", source = "student.major")
    @Mapping(target = "address", source = "student.address")
    @Mapping(target = "latitude", source = "student.latitude")
    @Mapping(target = "longitude", source = "student.longitude")
    @Mapping(target = "bio", source = "student.bio")
    @Mapping(target = "skills", source = "student.skills")
    @Mapping(target = "experience", source = "student.experience")
    @Mapping(target = "cvUrl", source = "student.cvUrl")
    StudentResponse toStudentResponse(Student student);
}
