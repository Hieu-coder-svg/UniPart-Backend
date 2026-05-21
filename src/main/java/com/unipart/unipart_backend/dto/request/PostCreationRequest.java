package com.unipart.unipart_backend.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostCreationRequest {
    List<Long> categoryIds;
    String content;
    String imageUrl;
    Long relatedJobId; // optional
}
