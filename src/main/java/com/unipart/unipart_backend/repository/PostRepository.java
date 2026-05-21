package com.unipart.unipart_backend.repository;

import com.unipart.unipart_backend.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    // Used by admin / internal only
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Fetch post with user eagerly loaded
    @Query("SELECT p FROM Post p JOIN FETCH p.user WHERE p.id = :id")
    Post findPostWithUserById(@Param("id") Long id);

    // Public feed – treat NULL as visible
    @Query("SELECT p FROM Post p WHERE p.isHide IS NULL OR p.isHide = false ORDER BY p.createdAt DESC")
    Page<Post> findAllVisibleOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.categoryId = :categoryId AND (p.isHide IS NULL OR p.isHide = false) ORDER BY p.createdAt DESC")
    Page<Post> findVisibleByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.userId = :userId AND (p.isHide IS NULL OR p.isHide = false) ORDER BY p.createdAt DESC")
    Page<Post> findVisibleByUserId(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')) AND (p.isHide IS NULL OR p.isHide = false) ORDER BY p.createdAt DESC")
    Page<Post> findVisibleByKeyword(@Param("keyword") String keyword, Pageable pageable);

    boolean existsByCategoryId(Long categoryId);
}
