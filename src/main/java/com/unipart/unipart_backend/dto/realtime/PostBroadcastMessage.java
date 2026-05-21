package com.unipart.unipart_backend.dto.realtime;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostBroadcastMessage {
    String type;           // "NEW_POST"
    Long postId;
    String authorId;
    String authorName;
    String authorAvatar;
    String authorRole;
    String categoryName;   // kept for backward compatibility
    List<String> categoryNames;
    String contentPreview; // 100 ký tự đầu
    LocalDateTime createdAt;
}
