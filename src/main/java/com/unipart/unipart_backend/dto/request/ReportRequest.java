package com.unipart.unipart_backend.dto.request;

import com.unipart.unipart_backend.enums.ReportTargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportRequest {

    @NotNull(message = "Target type is required")
    ReportTargetType targetType;

    @NotBlank(message = "Target ID is required")
    String targetId;

    @NotBlank(message = "Reason is required")
    String reason;

    String evidenceUrl;
}
