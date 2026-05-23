package com.unipart.unipart_backend.dto.response;

import com.unipart.unipart_backend.enums.ReviewType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewResponse {
    Long id;
    Long jobId;
    String studentId;
    String employerId;
    ReviewType reviewType;
    Integer rating;
    String comment;
    String studentName;
    String studentAvatar;
    String employerName;
    String employerAvatar;
    LocalDateTime createdAt;
}