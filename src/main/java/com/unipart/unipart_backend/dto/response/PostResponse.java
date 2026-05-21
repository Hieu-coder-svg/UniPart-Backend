package com.unipart.unipart_backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostResponse {
    Long id;
    String userId;
    String authorName;
    String authorAvatar;
    String authorRole;
    Long categoryId; // kept for backward compatibility
    String categoryName; // kept for backward compatibility
    List<Long> categoryIds;
    List<String> categoryNames;
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
