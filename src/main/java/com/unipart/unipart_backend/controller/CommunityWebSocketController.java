package com.unipart.unipart_backend.controller;

import com.unipart.unipart_backend.dto.realtime.CommentBroadcastMessage;
import com.unipart.unipart_backend.dto.realtime.LikeBroadcastMessage;
import com.unipart.unipart_backend.dto.realtime.PostBroadcastMessage;
import com.unipart.unipart_backend.dto.request.CommentRequest;
import com.unipart.unipart_backend.dto.request.PostCreationRequest;
import com.unipart.unipart_backend.dto.response.CommentResponse;
import com.unipart.unipart_backend.dto.response.PostResponse;
import com.unipart.unipart_backend.service.CommentService;
import com.unipart.unipart_backend.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CommunityWebSocketController {

    private final PostService postService;
    private final CommentService commentService;
    private final SimpMessagingTemplate messagingTemplate;

    // ===== Helper =====

    private String truncate(String text, int max) {
        return text != null && text.length() > max ? text.substring(0, max) + "..." : text;
    }

    private String getUserId(SimpMessageHeaderAccessor headerAccessor) {
        return (String) headerAccessor.getSessionAttributes().get("userId");
    }

    private String getUserRole(SimpMessageHeaderAccessor headerAccessor) {
        return (String) headerAccessor.getSessionAttributes().get("userRole");
    }

    // ===== NEW POST =====

    /**
     * Client đăng bài mới qua WebSocket.
     * → Broadcast đến tất cả subscriber của /topic/community/posts
     *
     * Client gửi: SEND /app/community/post/new-post {categoryId, content, relatedJobId?}
     */
    @MessageMapping("")
    public void handleNewPost(PostCreationRequest req,
                              SimpMessageHeaderAccessor headerAccessor) {
        String userId = getUserId(headerAccessor);
        if (userId == null) {
            log.warn("WebSocket handleNewPost: unauthenticated");
            return;
        }

        PostResponse saved = postService.create(req, userId);

        PostBroadcastMessage broadcast = PostBroadcastMessage.builder()
                .type("NEW_POST")
                .postId(saved.getId())
                .authorId(saved.getUserId())
                .authorName(saved.getAuthorName())
                .authorRole(saved.getAuthorRole())
                .categoryName(saved.getCategoryName())
                .contentPreview(truncate(saved.getContent(), 100))
                .createdAt(saved.getCreatedAt())
                .build();

        messagingTemplate.convertAndSend("/topic/community/posts", broadcast);
        log.debug("Broadcast new post id={} by userId={}", saved.getId(), userId);
    }

    // ===== NEW COMMENT =====

    /**
     * Client bình luận qua WebSocket.
     * → Broadcast đến subscriber của /topic/community/posts/{postId}
     *
     * Client gửi: SEND /app/community/comment/new-comment {postId, content, parentCommentId?}
     */
    @MessageMapping("/community/comment/new-comment")
    public void handleNewComment(CommentRequest req,
                                 SimpMessageHeaderAccessor headerAccessor) {
        String userId = getUserId(headerAccessor);
        if (userId == null) {
            log.warn("WebSocket handleNewComment: unauthenticated");
            return;
        }

        CommentResponse saved = commentService.add(req, userId);

        CommentBroadcastMessage broadcast = CommentBroadcastMessage.builder()
                .type("NEW_COMMENT")
                .postId(req.getPostId())
                .commentId(saved.getId())
                .authorId(saved.getUserId())
                .authorName(saved.getAuthorName())
                .content(saved.getContent())
                .parentCommentId(saved.getParentCommentId())
                .createdAt(saved.getCreatedAt())
                .build();

        messagingTemplate.convertAndSend("/topic/community/posts/" + req.getPostId(), broadcast);
        log.debug("Broadcast new comment id={} on postId={}", saved.getId(), req.getPostId());
    }

    // ===== LIKE TOGGLE =====

    /**
     * Client toggle like qua WebSocket.
     * → Broadcast like count mới đến subscriber của /topic/community/posts/{postId}
     *
     * Client gửi: SEND /app/community/post/{postId}/like (không cần body)
     */
    @MessageMapping("/community/post/{postId}/like")
    public void handleLike(@DestinationVariable Long postId,
                           SimpMessageHeaderAccessor headerAccessor) {
        String userId = getUserId(headerAccessor);
        if (userId == null) {
            log.warn("WebSocket handleLike: unauthenticated");
            return;
        }

        postService.likeToggle(postId, userId);
        int newCount = postService.getLikesCount(postId);
        boolean liked = postService.isLikedByUser(postId, userId);

        LikeBroadcastMessage broadcast = LikeBroadcastMessage.builder()
                .type("LIKE_UPDATE")
                .postId(postId)
                .likesCount(newCount)
                .liked(liked)
                .build();

        messagingTemplate.convertAndSend("/topic/community/posts/" + postId, broadcast);
        log.debug("Broadcast like update postId={}, count={}, liked={}", postId, newCount, liked);
    }
}
