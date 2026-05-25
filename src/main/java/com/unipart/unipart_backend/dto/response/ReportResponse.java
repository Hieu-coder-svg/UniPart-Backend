package com.unipart.unipart_backend.dto.response;

import com.unipart.unipart_backend.enums.ReportStatus;
import com.unipart.unipart_backend.enums.ReportTargetType;
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
public class ReportResponse {
    Long id;
    String reporterId;
    String reporterName;
    ReportTargetType targetType;
    String targetId;
    String targetName;
    String reason;
    String evidenceUrl;
    ReportStatus status;
    String adminNote;
    String resolvedBy;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
