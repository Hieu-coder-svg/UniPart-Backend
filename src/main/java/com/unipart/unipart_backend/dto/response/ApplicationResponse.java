package com.unipart.unipart_backend.dto.response;

import java.time.LocalDateTime;

public class ApplicationResponse {
    private Long id;
    private String studentId;
    private Long jobId;
    private String status;
    private LocalDateTime appliedAt;
    private LocalDateTime completedAt;
}
