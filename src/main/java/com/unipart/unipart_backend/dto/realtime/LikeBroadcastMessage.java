package com.unipart.unipart_backend.dto.realtime;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeBroadcastMessage {
    String type;         // "LIKE_UPDATE"
    Long postId;
    Integer likesCount;  // số like mới sau khi toggle
    Boolean liked;       // true = vừa like, false = vừa unlike
}
