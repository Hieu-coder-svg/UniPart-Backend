package com.unipart.unipart_backend.repository;

import com.unipart.unipart_backend.entity.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCategoryRepository extends JpaRepository<PostCategory, Long> {

    List<PostCategory> findByPostId(Long postId);

    @Modifying
    @Query("DELETE FROM PostCategory pc WHERE pc.postId = :postId")
    void deleteByPostId(@Param("postId") Long postId);

    @Modifying
    @Query("DELETE FROM PostCategory pc WHERE pc.postId = :postId AND pc.categoryId = :categoryId")
    void deleteByPostIdAndCategoryId(@Param("postId") Long postId, @Param("categoryId") Long categoryId);
}
