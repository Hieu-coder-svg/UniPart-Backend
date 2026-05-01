package com.unipart.unipart_backend.service;

import com.unipart.unipart_backend.dto.request.CommentRequest;
import com.unipart.unipart_backend.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {
    CommentResponse add(CommentRequest request, String userId);
    List<CommentResponse> getByPost(Long postId);
    void delete(Long id, String userId, String userRole);
}
