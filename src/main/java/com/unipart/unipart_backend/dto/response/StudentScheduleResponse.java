package com.unipart.unipart_backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
@Builder
public class StudentScheduleResponse {
    private String userId;
    private Map<String, Set<Long>> scheduleMatrix;
}
