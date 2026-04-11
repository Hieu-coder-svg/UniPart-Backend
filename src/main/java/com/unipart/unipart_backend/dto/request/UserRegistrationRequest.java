package com.unipart.unipart_backend.dto.request;

import com.unipart.unipart_backend.validator.DobContraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentRegistrationRequest {

    @NotBlank(message = "Username không được để trống")
    @Size(min = 4, max = 50, message = "USER_INVALID")
    private String username;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "EMAIL_INVALID")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 8, max = 32, message = "Mật khẩu phải có ít nhất 8 kí tự")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$",
            message = "Mật khẩu phải chứa ít nhất một chữ cái viết hoa và một ký tự đặc biệt."
    )
    private String password;
    @NotBlank(message = "Họ và tên không được để trống")
    @Size(max = 100, message = "Họ tên không được vượt quá 100 ký tự")
    private String fullName;
    @NotBlank(message = "Số điện thoại không được để trống")
    @Size(max = 10, message = "Họ tên không được vượt quá 10 ký tự")
    private String phoneNumber;
    @Past(message = "Vui lòng nhập đúng ngày sinh của bạn")
    @DobContraint(min = 16,message = "INVALID_DOB")
    private LocalDate dateOfBirth;

    private String address;

    private String phone;

}