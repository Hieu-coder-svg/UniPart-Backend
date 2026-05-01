package com.unipart.unipart_backend.dto.request;

import com.unipart.unipart_backend.enums.ReportStatus;
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
public class ReportUpdateRequest {

    @NotNull(message = "Status is required")
    ReportStatus status;

    String adminNote;
}
