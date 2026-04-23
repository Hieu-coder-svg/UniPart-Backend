package com.unipart.unipart_backend.dto.request;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentUpdateRequest {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 2, max = 100, message = "Họ tên phải từ 2 đến 100 ký tự")
    private String fullName;

    private LocalDate dateOfBirth;

    @Pattern(regexp = "^\\d{10}$", message = "Số điện thoại phải gồm 10 chữ số")
    private String phoneNumber;

    private String gender;

    @NotBlank(message = "Trường đại học không được để trống")
    private String university;

    @NotBlank(message = "Chuyên ngành không được để trống")
    private String major;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;

    @Min(value = -90, message = "Vĩ độ (Latitude) không hợp lệ")
    @Max(value = 90, message = "Vĩ độ (Latitude) không hợp lệ")
    private Double latitude;

    @Min(value = -180, message = "Kinh độ (Longitude) không hợp lệ")
    @Max(value = 180, message = "Kinh độ (Longitude) không hợp lệ")
    private Double longitude;
}