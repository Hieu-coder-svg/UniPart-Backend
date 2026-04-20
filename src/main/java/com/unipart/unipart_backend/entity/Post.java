package com.unipart.unipart_backend.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", length = 50, nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private Student student;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;


    @Column(name = "type", length = 20, nullable = false)
    private String type;

    @Column(name = "related_job_id")
    private Long relatedJobId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_job_id", insertable = false, updatable = false)
    private Job relatedJob;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
