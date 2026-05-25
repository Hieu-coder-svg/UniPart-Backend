package com.unipart.unipart_backend.dto.realtime;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentBroadcastMessage {
    String type;           // "NEW_COMMENT"
    Long postId;
    Long commentId;
    String authorId;
    String authorName;
    String authorAvatar;
    String content;
    Long parentCommentId;  // null = comment gốc
    LocalDateTime createdAt;
}
