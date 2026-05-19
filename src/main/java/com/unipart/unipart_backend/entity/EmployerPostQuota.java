package com.unipart.unipart_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "employer_post_quota")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployerPostQuota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", nullable = false)
    private Employer employer;

    @Column(name = "quota_type", nullable = false)
    private String quotaType;

    @Column(name = "remaining_posts", nullable = false)
    private Integer remainingPosts;

    @Column(name = "max_posts_per_day")
    private Integer maxPostsPerDay;
    @Column(name = "type")
    private String type;
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "used_posts_today")
    private Integer usedPostsToday;

    @Column(name = "last_reset_date")
    private java.time.LocalDate lastResetDate;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
