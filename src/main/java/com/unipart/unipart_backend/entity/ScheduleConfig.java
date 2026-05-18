package com.unipart.unipart_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "schedule_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleConfig {

    @Id
    @Builder.Default
    private Long id = 1L;

    @Column(name = "full_enabled")
    private boolean fullEnabled;

    @Column(name = "full_time")
    private String fullTime;

    @Column(name = "full_frequency")
    private String fullFrequency;

    @Column(name = "incremental_enabled")
    private boolean incrementalEnabled;

    @Column(name = "incremental_every")
    private String incrementalEvery;
}
