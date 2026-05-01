package com.unipart.unipart_backend.mapper;

import com.unipart.unipart_backend.dto.response.ReviewResponse;
import com.unipart.unipart_backend.entity.Review;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    ReviewResponse toResponse(Review review);

    List<ReviewResponse> toResponseList(List<Review> reviews);
}
