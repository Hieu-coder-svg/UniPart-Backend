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

    @Mapping(target = "id", source = "employer.user.id")
    @Mapping(target = "username", source = "employer.user.username")
    @Mapping(target = "email", source = "employer.user.email")
    @Mapping(target = "avatar", source = "employer.user.avatar")
    @Mapping(target = "fullName", source = "employer.user.fullName")
    @Mapping(target = "dateOfBirth", source = "employer.user.dateOfBirth")
    @Mapping(target = "phoneNumber", source = "employer.user.phoneNumber")
    @Mapping(target = "gender", source = "employer.user.gender")
    @Mapping(target = "isBlocked", source = "employer.user.isBlocked")
    @Mapping(target = "isActived", source = "employer.user.isActived")
    @Mapping(target = "createdAt", source = "employer.user.createdAt")
    @Mapping(target = "updatedAt", source = "employer.user.updatedAt")

    @Mapping(target = "companyName", source = "employer.companyName")
    @Mapping(target = "companyAddress", source = "employer.companyAddress")
    @Mapping(target = "latitude", source = "employer.latitude")
    @Mapping(target = "longitude", source = "employer.longitude")
    @Mapping(target = "description", source = "employer.description")
    @Mapping(target = "rating", source = "employer.rating")
    EmployerResponse toEmployerResponse(Employer employer);
}