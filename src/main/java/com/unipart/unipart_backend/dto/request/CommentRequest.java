package com.unipart.unipart_backend.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentRequest {
    Long postId;
    String content;
    Long parentCommentId; // null = comment gốc, có value = reply
    String imageUrl;
}
