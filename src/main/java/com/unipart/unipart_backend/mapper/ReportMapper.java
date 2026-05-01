package com.unipart.unipart_backend.mapper;

import com.unipart.unipart_backend.dto.response.ReportResponse;
import com.unipart.unipart_backend.entity.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    @Mapping(target = "reporterName", source = "reporter.fullName")
    ReportResponse toResponse(Report report);

    List<ReportResponse> toResponseList(List<Report> reports);
}
