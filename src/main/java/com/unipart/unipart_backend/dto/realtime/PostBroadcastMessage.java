package com.unipart.unipart_backend.dto.realtime;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostBroadcastMessage {
    String type;           // "NEW_POST"
    Long postId;
    String authorId;
    String authorName;
    String authorRole;
    String categoryName;
    String contentPreview; // 100 ký tự đầu
    LocalDateTime createdAt;
}
