package com.unipart.unipart_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobCreationRequest {

    @NotBlank(message = "Tiêu đề công việc không được để trống")
    private String title;

    private String image;

    private String description;

    private String workingShift;

    @NotNull(message = "Số lượng tuyển dụng không được để trống")
    @Positive(message = "Số lượng tuyển dụng phải là số dương")
    private Integer vacancies;

    private Boolean urgent;

    private String address;

    private BigDecimal locationLatitude;

    private BigDecimal locationLongitude;

    @NotNull(message = "Mức lương không được để trống")
    private BigDecimal salary;

    private LocalDateTime expiredAt;

    private List<JobTimeSlotRequest> timeSlots;
}
