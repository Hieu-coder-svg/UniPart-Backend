package com.unipart.unipart_backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class ApplicationResponse {
    Long id;
    Long jobId;
    String jobTitle;
    String studentId;
    String studentName;
    String studentAvatar;
    String studentEmail;
    String studentPhone;
    String studentUniversity;
    String studentMajor;
    String status;
    LocalDateTime appliedAt;
    LocalDateTime completedAt;
}