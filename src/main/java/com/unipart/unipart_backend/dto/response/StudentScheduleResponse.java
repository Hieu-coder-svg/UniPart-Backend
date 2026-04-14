package com.unipart.unipart_backend.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentUpdateScheduleRequest {
    private Long userId;
    private List<DaySchedule> schedules;

    @Data
    public static class DaySchedule {
        private DayOfWeek dayOfWeek;
        private Set<Long> busySlotIds;
    }
}
