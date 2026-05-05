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
public class CommentResponse {
    Long id;
    Long postId;
    String userId;
    String authorName;
    String content;
    Long parentCommentId;
    List<CommentResponse> replies;
    LocalDateTime createdAt;
}
