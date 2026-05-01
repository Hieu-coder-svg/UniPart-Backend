package com.unipart.unipart_backend.service.ServiceImpl;

import com.unipart.unipart_backend.dto.request.PostCreationRequest;
import com.unipart.unipart_backend.dto.request.PostUpdateRequest;
import com.unipart.unipart_backend.dto.response.PostResponse;
import com.unipart.unipart_backend.entity.Post;
import com.unipart.unipart_backend.entity.PostLike;
import com.unipart.unipart_backend.exception.AppException;
import com.unipart.unipart_backend.exception.ErrorCode;
import com.unipart.unipart_backend.repository.CategoryRepository;
import com.unipart.unipart_backend.repository.PostLikeRepository;
import com.unipart.unipart_backend.repository.PostRepository;
import com.unipart.unipart_backend.repository.UserRepository;
import com.unipart.unipart_backend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    // ===== Helper =====

    private String getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaimAsString("userId");
        }
        return null;
    }

    private PostResponse toDTO(Post p, String currentUserId) {
        String categoryName = p.getCategory() != null ? p.getCategory().getCategoryName() : null;
        String authorName = p.getUser() != null ? p.getUser().getFullName() : null;
        String authorRole = (p.getUser() != null && p.getUser().getRole() != null)
                ? p.getUser().getRole().getName() : null;
        boolean liked = currentUserId != null
                && postLikeRepository.existsByPostIdAndUserId(p.getId(), currentUserId);

        return PostResponse.builder()
                .id(p.getId())
                .userId(p.getUserId())
                .authorName(authorName)
                .authorRole(authorRole)
                .categoryId(p.getCategoryId())
                .categoryName(categoryName)
                .content(p.getContent())
                .relatedJobId(p.getRelatedJobId())
                .likesCount(p.getLikesCount())
                .commentsCount(p.getCommentsCount())
                .sharesCount(p.getSharesCount())
                .isLikedByMe(liked)
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    // ===== CRUD =====

    @Override
    @Transactional
    public PostResponse create(PostCreationRequest request, String userId) {
        if (!categoryRepository.existsById(request.getCategoryId())) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        Post post = Post.builder()
                .userId(userId)
                .categoryId(request.getCategoryId())
                .content(request.getContent())
                .relatedJobId(request.getRelatedJobId())
                .build();
        Post saved = postRepository.save(post);
        // reload để lấy lazy relations
        return toDTO(postRepository.findById(saved.getId()).orElse(saved), userId);
    }

    @Override
    @Transactional
    public PostResponse update(Long id, PostUpdateRequest request, String userId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        if (!post.getUserId().equals(userId)) {
            throw new AppException(ErrorCode.POST_FORBIDDEN);
        }
        if (request.getContent() != null) post.setContent(request.getContent());
        if (request.getCategoryId() != null) {
            if (!categoryRepository.existsById(request.getCategoryId())) {
                throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
            }
            post.setCategoryId(request.getCategoryId());
        }
        return toDTO(postRepository.save(post), userId);
    }

    @Override
    @Transactional
    public void delete(Long id, String userId, String userRole) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        boolean isOwner = post.getUserId().equals(userId);
        boolean isAdmin = "ADMIN".equals(userRole);
        if (!isOwner && !isAdmin) {
            throw new AppException(ErrorCode.POST_FORBIDDEN);
        }
        postRepository.deleteById(id);
    }

    // ===== GET =====

    @Override
    public Page<PostResponse> getFeed(Pageable pageable, String currentUserId) {
        return postRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(p -> toDTO(p, currentUserId));
    }

    @Override
    public Page<PostResponse> getByCategory(Long categoryId, Pageable pageable, String currentUserId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        return postRepository.findByCategoryIdOrderByCreatedAtDesc(categoryId, pageable)
                .map(p -> toDTO(p, currentUserId));
    }

    @Override
    public Page<PostResponse> getByUser(String userId, Pageable pageable, String currentUserId) {
        return postRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(p -> toDTO(p, currentUserId));
    }

    @Override
    public PostResponse getById(Long id, String currentUserId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        return toDTO(post, currentUserId);
    }

    @Override
    public Page<PostResponse> search(String keyword, Pageable pageable, String currentUserId) {
        return postRepository.findByContentContainingIgnoreCaseOrderByCreatedAtDesc(keyword, pageable)
                .map(p -> toDTO(p, currentUserId));
    }

    // ===== LIKE =====

    @Override
    @Transactional
    public int likeToggle(Long postId, String userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        postLikeRepository.findByPostIdAndUserId(postId, userId).ifPresentOrElse(
                like -> {
                    // Already liked → unlike
                    postLikeRepository.delete(like);
                    post.setLikesCount(Math.max(0, post.getLikesCount() - 1));
                },
                () -> {
                    // Not liked → like
                    postLikeRepository.save(PostLike.builder()
                            .postId(postId)
                            .userId(userId)
                            .build());
                    post.setLikesCount(post.getLikesCount() + 1);
                }
        );
        postRepository.save(post);
        return post.getLikesCount();
    }

    @Override
    public boolean isLikedByUser(Long postId, String userId) {
        return postLikeRepository.existsByPostIdAndUserId(postId, userId);
    }

    // ===== SHARE =====

    @Override
    @Transactional
    public PostResponse share(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        post.setSharesCount(post.getSharesCount() + 1);
        return toDTO(postRepository.save(post), null);
    }
}
