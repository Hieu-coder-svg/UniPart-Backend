package com.unipart.unipart_backend.mapper;

import com.unipart.unipart_backend.dto.response.StudentScheduleResponse;
import com.unipart.unipart_backend.entity.StudentSchedule;
import com.unipart.unipart_backend.entity.TimeSlot;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
@Mapper(componentModel = "spring")
public interface StudentScheduleMapper {

    default StudentScheduleResponse toResponse(String userId, List<StudentSchedule> entities) {
        if (entities == null) return null;

        Map<String, Set<Long>> matrix = entities.stream()
                .collect(Collectors.toMap(
                        StudentSchedule::getDayOfWeek,
                        entity -> entity.getBusyTimeSlots().stream()
                                .map(TimeSlot::getId)
                                .collect(Collectors.toSet())
                ));

        return StudentScheduleResponse.builder()
                .userId(userId)
                .scheduleMatrix(matrix)
                .build();
    }

}