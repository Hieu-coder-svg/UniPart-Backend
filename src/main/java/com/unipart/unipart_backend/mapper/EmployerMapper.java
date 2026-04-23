package com.unipart.unipart_backend.mapper;
import com.unipart.unipart_backend.dto.request.EmployerRegistrationRequest;
import com.unipart.unipart_backend.dto.request.EmployerUpdateRequest;
import com.unipart.unipart_backend.dto.response.EmployerResponse;
import com.unipart.unipart_backend.entity.Employer;
import com.unipart.unipart_backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmployerMapper {

    @Mapping(target = "passwordHash", ignore = true) //
    User toUserEntity(EmployerRegistrationRequest request);
    Employer toEmployerEntity(EmployerRegistrationRequest request);

    void updateUserFromRequest(EmployerUpdateRequest request, @MappingTarget User user);

    void updateEmployerFromRequest(EmployerUpdateRequest request, @MappingTarget Employer employer);

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "fullName", source = "user.fullName")
    @Mapping(target = "companyName", source = "employer.companyName")
    @Mapping(target = "companyAddress", source = "employer.companyAddress")
    EmployerResponse toEmployerResponse(User user);
}