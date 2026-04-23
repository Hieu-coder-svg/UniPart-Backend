package com.unipart.unipart_backend.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedJobResponse {
    private Long id;
    private String studentId;
    private Long jobId;
    private LocalDateTime savedAt;
}