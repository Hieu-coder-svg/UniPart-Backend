package com.unipart.unipart_backend.service;

import com.unipart.unipart_backend.dto.request.PostCreationRequest;
import com.unipart.unipart_backend.dto.request.PostUpdateRequest;
import com.unipart.unipart_backend.dto.response.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    PostResponse create(PostCreationRequest request, String userId);
    PostResponse update(Long id, PostUpdateRequest request, String userId);
    void delete(Long id, String userId, String userRole);
    Page<PostResponse> getFeed(Pageable pageable, String currentUserId);
    Page<PostResponse> getByCategory(Long categoryId, Pageable pageable, String currentUserId);
    Page<PostResponse> getByUser(String userId, Pageable pageable, String currentUserId);
    PostResponse getById(Long id, String currentUserId);
    Page<PostResponse> search(String keyword, Pageable pageable, String currentUserId);
    boolean likeToggle(Long postId, String userId);
    boolean isLikedByUser(Long postId, String userId);
    Integer getLikesCount(Long postId);
    PostResponse share(Long postId);
}
