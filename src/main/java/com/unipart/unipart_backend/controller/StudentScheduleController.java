package com.unipart.unipart_backend.controller;

import com.unipart.unipart_backend.dto.request.StudentScheduleRequest;
import com.unipart.unipart_backend.dto.response.ApiResponse;
import com.unipart.unipart_backend.dto.response.StudentScheduleResponse;
import com.unipart.unipart_backend.service.StudentScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/my-schedule")
@RequiredArgsConstructor
public class StudentScheduleController {
    private final StudentScheduleService studentScheduleService;
    @PostMapping
    public ApiResponse<StudentScheduleResponse> saveFullSchedule(@RequestBody StudentScheduleRequest request) {
        return ApiResponse.<StudentScheduleResponse>builder()
                .result(studentScheduleService.saveFullSchedule(request))
                .build();
    }
    @GetMapping
    public ApiResponse<StudentScheduleResponse> getStudentSchedules() {
        return ApiResponse.<StudentScheduleResponse>builder()
                .result(studentScheduleService.getStudentSchedulesByUserId())
                .build();
    }
}
