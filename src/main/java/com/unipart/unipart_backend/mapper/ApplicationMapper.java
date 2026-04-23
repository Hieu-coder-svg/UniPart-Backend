package com.unipart.unipart_backend.mapper;
import com.unipart.unipart_backend.dto.request.ApplyJobRequest;
import com.unipart.unipart_backend.dto.request.ApplyJobUpdateRequest;
import com.unipart.unipart_backend.dto.response.ApplicationResponse;
import com.unipart.unipart_backend.entity.Application;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "job", ignore = true)
    @Mapping(target = "appliedAt", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    Application toEntity(ApplyJobRequest request);

    ApplicationResponse toResponse(Application application);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "job", ignore = true)
    @Mapping(target = "appliedAt", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    void updateEntity(@MappingTarget Application application, ApplyJobUpdateRequest request);
}
