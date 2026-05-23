package com.unipart.unipart_backend.mapper;

import com.unipart.unipart_backend.dto.response.ReviewResponse;
import com.unipart.unipart_backend.entity.Review;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @org.mapstruct.Mapping(source = "student.user.fullName", target = "studentName")
    @org.mapstruct.Mapping(source = "student.user.avatar", target = "studentAvatar")
    @org.mapstruct.Mapping(source = "employer.user.fullName", target = "employerName")
    @org.mapstruct.Mapping(source = "employer.user.avatar", target = "employerAvatar")
    ReviewResponse toResponse(Review review);

    List<ReviewResponse> toResponseList(List<Review> reviews);
}
