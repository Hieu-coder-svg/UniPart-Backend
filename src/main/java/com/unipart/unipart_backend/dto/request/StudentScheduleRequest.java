package com.unipart.unipart_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentScheduleRequest {
    private List<DayScheduleRequest> schedules;

    @Data
    public static class DayScheduleRequest {
        private String dayOfWeek; // Ví dụ: "Thứ 2", "Thứ 3"...
        private Set<Long> busyTimeSlotIds; // Danh sách ID các khung giờ được chọn
    }
}
