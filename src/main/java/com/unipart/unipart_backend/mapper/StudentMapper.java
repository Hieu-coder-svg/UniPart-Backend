package com.unipart.unipart_backend.mapper;

import com.unipart.unipart_backend.dto.request.StudentRegistrationRequest;
import com.unipart.unipart_backend.dto.request.StudentUpdateRequest;
import com.unipart.unipart_backend.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StudentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)           // Sẽ gán sau khi save User
    Student toStudentEntity(StudentRegistrationRequest request);
    void updateStudentFromRequest(StudentUpdateRequest request, @MappingTarget Student student);
}
