package com.unipart.unipart_backend.mapper;

import com.unipart.unipart_backend.dto.request.SavedJobRequest;
import com.unipart.unipart_backend.dto.response.SavedJobResponse;
import com.unipart.unipart_backend.entity.SavedJob;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SavedJobMapper {

    SavedJob toEntity(SavedJobRequest request);
    SavedJobResponse toDto(SavedJob savedJob);
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(SavedJobRequest request, @MappingTarget SavedJob entity);
}