package com.unipart.unipart_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "backup_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BackupHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type; // "full" or "incremental"

    @Column(nullable = false)
    private String status; // "completed", "failed", "running"

    @Column(nullable = false)
    private LocalDateTime date;

    @Column
    private String duration; // e.g., "15 giây"

    @Column(name = "file_name")
    private String fileName;
}
