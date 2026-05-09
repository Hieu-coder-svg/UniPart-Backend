package com.unipart.unipart_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemLogDTO {
    private String id;
    private String level;
    private String message;
    private String timestamp;
    private String source;
    private String details;
}
