package com.unipart.unipart_backend.service.ServiceImpl;

import com.unipart.unipart_backend.dto.request.CommentRequest;
import com.unipart.unipart_backend.dto.response.CommentResponse;
import com.unipart.unipart_backend.entity.Comment;
import com.unipart.unipart_backend.entity.Post;
import com.unipart.unipart_backend.exception.AppException;
import com.unipart.unipart_backend.exception.ErrorCode;
import com.unipart.unipart_backend.repository.CommentRepository;
import com.unipart.unipart_backend.repository.PostRepository;
import com.unipart.unipart_backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    private CommentResponse toDTO(Comment c) {
        String authorName = c.getUser() != null ? c.getUser().getFullName() : null;
        return CommentResponse.builder()
                .id(c.getId())
                .postId(c.getPostId())
                .userId(c.getUserId())
                .authorName(authorName)
                .content(c.getContent())
                .imageUrl(c.getImageUrl())
                .parentCommentId(c.getParentCommentId())
                .createdAt(c.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public CommentResponse add(CommentRequest request, String userId) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        // Kiểm tra reply chỉ 1 cấp
        if (request.getParentCommentId() != null) {
            Comment parent = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
            if (parent.getParentCommentId() != null) {
                throw new AppException(ErrorCode.REPLY_DEPTH_EXCEEDED);
            }
        }

        Comment comment = Comment.builder()
                .postId(request.getPostId())
                .userId(userId)
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .parentCommentId(request.getParentCommentId())
                .build();

        Comment saved = commentRepository.save(comment);

        // Cập nhật commentsCount của post
        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.save(post);

        return toDTO(commentRepository.findById(saved.getId()).orElse(saved));
    }

    @Override
    public List<CommentResponse> getByPost(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new AppException(ErrorCode.POST_NOT_FOUND);
        }
        // Lấy tất cả comments (flatten - bao gồm cả replies)
        return commentRepository
                .findByPostIdOrderByCreatedAtAsc(postId)
                .stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional
    public void delete(Long id, String userId, String userRole) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

        boolean isOwner = comment.getUserId().equals(userId);
        boolean isAdmin = "ADMIN".equals(userRole);
        if (!isOwner && !isAdmin) {
            throw new AppException(ErrorCode.COMMENT_FORBIDDEN);
        }

        // Giảm commentsCount
        postRepository.findById(comment.getPostId()).ifPresent(post -> {
            post.setCommentsCount(Math.max(0, post.getCommentsCount() - 1));
            postRepository.save(post);
        });

        commentRepository.deleteById(id);
    }
}
