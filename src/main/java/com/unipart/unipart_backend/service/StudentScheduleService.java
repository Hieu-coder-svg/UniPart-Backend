package com.unipart.unipart_backend.service;

import com.unipart.unipart_backend.dto.request.StudentScheduleRequest;
import com.unipart.unipart_backend.dto.response.StudentScheduleResponse;

import java.util.List;

public interface StudentScheduleService {
    StudentScheduleResponse saveFullSchedule(StudentScheduleRequest request);
    StudentScheduleResponse getStudentSchedulesByUserId();
}
