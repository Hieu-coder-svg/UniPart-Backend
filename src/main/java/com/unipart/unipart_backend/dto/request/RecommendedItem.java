package com.unipart.unipart_backend.dto.request;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendedItem {
    private String id;
    private String title;
    private String type;
    private String reason;
}