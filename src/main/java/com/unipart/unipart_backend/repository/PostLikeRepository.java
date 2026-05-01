package com.unipart.unipart_backend.repository;

import com.unipart.unipart_backend.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByPostIdAndUserId(Long postId, String userId);

    Optional<PostLike> findByPostIdAndUserId(Long postId, String userId);
}
