package com.unipart.unipart_backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostResponse {
    Long id;
    String userId;
    String authorName;
    String authorRole;
    Long categoryId;
    String categoryName;
    String content;
    String imageUrl;
    Long relatedJobId;
    Integer likesCount;
    Integer commentsCount;
    Integer sharesCount;
    Boolean isLikedByMe;
    Boolean isHide;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
