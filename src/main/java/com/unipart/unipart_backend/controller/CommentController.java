package com.unipart.unipart_backend.controller;

import com.unipart.unipart_backend.dto.request.CommentRequest;
import com.unipart.unipart_backend.dto.response.ApiResponse;
import com.unipart.unipart_backend.dto.response.CommentResponse;
import com.unipart.unipart_backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

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

    @GetMapping("/post/{postId}")
    public ApiResponse<List<CommentResponse>> getByPost(@PathVariable Long postId) {
        return ApiResponse.<List<CommentResponse>>builder()
                .result(commentService.getByPost(postId))
                .build();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'EMPLOYER')")
    public ApiResponse<CommentResponse> add(@RequestBody CommentRequest request) {
        return ApiResponse.<CommentResponse>builder()
                .result(commentService.add(request, getCurrentUserId()))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'EMPLOYER', 'ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        commentService.delete(id, getCurrentUserId(), getCurrentUserRole());
        return ApiResponse.<Void>builder().build();
    }
}
