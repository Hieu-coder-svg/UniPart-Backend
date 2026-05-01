package com.unipart.unipart_backend.mapper;

import com.unipart.unipart_backend.dto.request.PackageRequest;
import com.unipart.unipart_backend.dto.response.PackageResponse;
import com.unipart.unipart_backend.entity.SubscriptionPackage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PackageMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    SubscriptionPackage toEntity(PackageRequest request);

    PackageResponse toResponse(SubscriptionPackage entity);

    List<PackageResponse> toResponseList(List<SubscriptionPackage> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(@MappingTarget SubscriptionPackage entity, PackageRequest request);
}
