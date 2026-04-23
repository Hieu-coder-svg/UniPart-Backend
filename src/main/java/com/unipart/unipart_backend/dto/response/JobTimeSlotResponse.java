package com.unipart.unipart_backend.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

public class JobTimeSlotResponse {
    private Long id;
    private LocalDate workDate;
    private LocalTime startTime;
    private LocalTime endTime;
}
