package com.unipart.unipart_backend.service.ServiceImpl;

import com.unipart.unipart_backend.dto.request.EmployerRegistrationRequest;
import com.unipart.unipart_backend.dto.request.EmployerUpdateRequest;
import com.unipart.unipart_backend.dto.request.SendOTPRequest;
import com.unipart.unipart_backend.dto.request.StudentRegistrationRequest;
import com.unipart.unipart_backend.dto.request.StudentUpdateRequest;
import com.unipart.unipart_backend.dto.request.UserUpdateRequest;
import com.unipart.unipart_backend.dto.response.EmployerResponse;
import com.unipart.unipart_backend.dto.response.StudentResponse;
import com.unipart.unipart_backend.dto.response.UserResponse;
import com.unipart.unipart_backend.entity.Employer;
import com.unipart.unipart_backend.entity.Role;
import com.unipart.unipart_backend.entity.Student;
import com.unipart.unipart_backend.entity.User;
import com.unipart.unipart_backend.exception.AppException;
import com.unipart.unipart_backend.exception.ErrorCode;
import com.unipart.unipart_backend.mapper.EmployerMapper;
import com.unipart.unipart_backend.mapper.StudentMapper;
import com.unipart.unipart_backend.mapper.UserMapper;
import com.unipart.unipart_backend.repository.EmployerRepository;
import com.unipart.unipart_backend.repository.RoleRepository;
import com.unipart.unipart_backend.repository.StudentRepository;
import com.unipart.unipart_backend.repository.UserRepository;
import com.unipart.unipart_backend.service.OtpService;
import com.unipart.unipart_backend.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal=true)
@Service
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    UserMapper userMapper;
    StudentMapper studentMapper;
    PasswordEncoder passwordEncoder;
    StudentRepository studentRepository;
    RoleRepository roleRepository;
    OtpService otpService;
    EmployerMapper employerMapper;
    EmployerRepository employerRepository;
    public StudentResponse registerStudent(StudentRegistrationRequest request) {

        User user = userMapper.toUserEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        var role = roleRepository.findById(2).orElseThrow();
        user.setRole(role);
        user = userRepository.save(user);
        SendOTPRequest otpRequest = new SendOTPRequest(request.getEmail());
        otpService.generateAndSendOtp(otpRequest);

        Student student = studentMapper.toStudentEntity(request);
        student.setUser(user);

        student = studentRepository.save(student);
        return userMapper.toStudentResponse(user);
    }
    @PreAuthorize("hasRole('STUDENT')")
    public StudentResponse updateProfileStudent(StudentUpdateRequest request) {
        var context = SecurityContextHolder.getContext();
       String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() ->new AppException(ErrorCode.USER_NOT_EXIST));

        userMapper.updateUserFromRequest(request, user);
        userRepository.save(user);
        var student = studentRepository.findByUser(user);
        studentMapper.updateStudentFromRequest(request,student);
        studentRepository.save(student);
        return userMapper.toStudentResponse(user);
    }
    public EmployerResponse registerEmployer(EmployerRegistrationRequest request) {
        User user = employerMapper.toUserEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setIsActived(false); // Đợi xác thực OTP
        user.setIsBlocked(false);

        Role role = roleRepository.findById(3)
                .orElseThrow();
        user.setRole(role);

        user = userRepository.save(user);
        otpService.generateAndSendOtp(new SendOTPRequest(request.getEmail()));
        Employer employer = employerMapper.toEmployerEntity(request);
        employer.setUser(user);
        employer.setId(user.getId());
        employerRepository.save(employer);
        return employerMapper.toEmployerResponse(user);
    }
    public EmployerResponse updateProfileEmployer(EmployerUpdateRequest request) {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        Employer employer = employerRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin nhà tuyển dụng"));
        employerMapper.updateUserFromRequest(request, user);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        employerMapper.updateEmployerFromRequest(request, employer);
        employerRepository.save(employer);
        return employerMapper.toEmployerResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAll(){

        return userMapper.toUserResponseList(userRepository.findAll());
    }
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse findUser(String id){
        User u = userRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXIST));
        return userMapper.toUserResponse(u);
    }
    @PreAuthorize("hasRole('STUDENT')")
    public StudentResponse getStudentMyInfo(){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

       User user =  userRepository.findByUsername(name)
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXIST));
        var student = studentRepository.findByUser(user);
        return userMapper.toStudentResponse(user);
    }
    @PreAuthorize("hasRole('EMPLOYER')")
    public EmployerResponse getEmployerMyInfo(){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user =  userRepository.findByUsername(name)
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXIST));
        var employer = employerRepository.findByUser(user);
        return employerMapper.toEmployerResponse(user);
    }
}
