package com.unipart.unipart_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "saved_job")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Column(name = "saved_at")
    private LocalDateTime savedAt;

    @PrePersist
    protected void onCreate() {
        savedAt = LocalDateTime.now();
    }
}