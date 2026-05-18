package com.unipart.unipart_backend.controller;

import com.unipart.unipart_backend.dto.request.PostCreationRequest;
import com.unipart.unipart_backend.dto.request.PostFilterRequest;
import com.unipart.unipart_backend.dto.request.PostUpdateRequest;
import com.unipart.unipart_backend.dto.response.ApiResponse;
import com.unipart.unipart_backend.dto.response.PostLikeResponse;
import com.unipart.unipart_backend.dto.response.PostResponse;
import com.unipart.unipart_backend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    private String getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaimAsString("userId");
        }
        return null;
    }

    private String getCurrentUserRole() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaimAsString("scope");
        }
        return null;
    }

    // ===== GET (PUBLIC) =====

    @GetMapping
    public ApiResponse<Page<PostResponse>> getFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {

        String userId = getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);

        Page<PostResponse> result;
        if (keyword != null && !keyword.isBlank()) {
            result = postService.search(keyword, pageable, userId);
        } else if (categoryId != null) {
            result = postService.getByCategory(categoryId, pageable, userId);
        } else {
            result = postService.getFeed(pageable, userId);
        }
        return ApiResponse.<Page<PostResponse>>builder().result(result).build();
    }

    @GetMapping("/{id}")
    public ApiResponse<PostResponse> getById(@PathVariable Long id) {
        return ApiResponse.<PostResponse>builder()
                .result(postService.getById(id, getCurrentUserId()))
                .build();
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<Page<PostResponse>> getByUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<Page<PostResponse>>builder()
                .result(postService.getByUser(userId, PageRequest.of(page, size), getCurrentUserId()))
                .build();
    }

    // ===== WRITE (AUTH) =====

    @PostMapping("/filter")
    public ApiResponse<Page<PostResponse>> filterPosts(@RequestBody PostFilterRequest request) {
        String userId = getCurrentUserId();
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        Page<PostResponse> result;
        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            result = postService.search(request.getKeyword(), pageable, userId);
        } else if (request.getCategoryId() != null) {
            result = postService.getByCategory(request.getCategoryId(), pageable, userId);
        } else {
            result = postService.getFeed(pageable, userId);
        }
        return ApiResponse.<Page<PostResponse>>builder().result(result).build();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'EMPLOYER')")
    public ApiResponse<PostResponse> create(@RequestBody PostCreationRequest request) {
        return ApiResponse.<PostResponse>builder()
                .result(postService.create(request, getCurrentUserId()))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'EMPLOYER')")
    public ApiResponse<PostResponse> update(@PathVariable Long id,
                                            @RequestBody PostUpdateRequest request) {
        return ApiResponse.<PostResponse>builder()
                .result(postService.update(id, request, getCurrentUserId()))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'EMPLOYER', 'ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        postService.delete(id, getCurrentUserId(), getCurrentUserRole());
        return ApiResponse.<Void>builder().build();
    }

    // ===== LIKE / SHARE =====

    @PostMapping("/{id}/like")
    @PreAuthorize("hasAnyRole('STUDENT', 'EMPLOYER')")
    public ApiResponse<PostLikeResponse> like(@PathVariable Long id) {
        Boolean liked = postService.likeToggle(id, getCurrentUserId());
        Integer likesCount = postService.getLikesCount(id);
        PostLikeResponse response = PostLikeResponse.builder()
                .postId(id)
                .liked(liked)
                .likesCount(likesCount)
                .build();
        return ApiResponse.<PostLikeResponse>builder().result(response).build();
    }

    @PostMapping("/{id}/share")
    @PreAuthorize("hasAnyRole('STUDENT', 'EMPLOYER')")
    public ApiResponse<PostResponse> share(@PathVariable Long id) {
        return ApiResponse.<PostResponse>builder()
                .result(postService.share(id))
                .build();
    }
}
