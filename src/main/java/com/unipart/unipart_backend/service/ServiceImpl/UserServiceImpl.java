package com.unipart.unipart_backend.service.ServiceImpl;

import com.unipart.unipart_backend.dto.request.ChangePasswordRequest;
import com.unipart.unipart_backend.dto.request.EmployerRegistrationRequest;
import com.unipart.unipart_backend.dto.request.EmployerUpdateRequest;
import com.unipart.unipart_backend.dto.request.ForgotPasswordRequest;
import com.unipart.unipart_backend.dto.request.SendOTPRequest;
import com.unipart.unipart_backend.dto.request.StudentRegistrationRequest;
import com.unipart.unipart_backend.dto.request.StudentUpdateRequest;
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
import com.unipart.unipart_backend.repository.EmployerPostQuotaRepository;
import com.unipart.unipart_backend.repository.EmployerPackagePurchaseRepository;
import com.unipart.unipart_backend.entity.EmployerPostQuota;
import com.unipart.unipart_backend.entity.EmployerPackagePurchase;
import com.unipart.unipart_backend.service.OtpService;
import com.unipart.unipart_backend.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;


@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class UserServiceImpl implements UserService {
    final UserRepository userRepository;
    final UserMapper userMapper;
    final StudentMapper studentMapper;
    final PasswordEncoder passwordEncoder;
    final StudentRepository studentRepository;
    final RoleRepository roleRepository;
    final OtpService otpService;
    final EmployerMapper employerMapper;
    final EmployerRepository employerRepository;
    final JavaMailSender mailSender;
    final EmployerPostQuotaRepository employerPostQuotaRepository;
    final EmployerPackagePurchaseRepository employerPackagePurchaseRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;
    private User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
    }
    @Transactional
    public StudentResponse registerStudent(StudentRegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        User user = userMapper.toUserEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setIsActived(false);
        user.setIsBlocked(false);
        var role = roleRepository.findByName("STUDENT")
                .orElseThrow(() -> new RuntimeException("Lỗi hệ thống: Chưa khởi tạo quyền STUDENT trong CSDL"));
        user.setRole(role);
        user = userRepository.save(user);
        Student student = studentMapper.toStudentEntity(request);
        student.setUser(user);
        student = studentRepository.save(student);
        SendOTPRequest otpRequest = new SendOTPRequest(request.getEmail());
        otpService.generateAndSendOtp(otpRequest);

        return studentMapper.toStudentResponse(student);
    }

    private void sendNewPasswordEmail(String toEmail, String newPassword) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Mật khẩu mới của bạn cho tài khoản Unipart");

            String htmlContent = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;">
                    <h2 style="color: #2c3e50;">Đặt lại mật khẩu tài khoản Unipart</h2>
                    <p>Xin chào,</p>
                    <p>Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn. Mật khẩu mới của bạn là:</p>
                    
                    <div style="text-align: center; margin: 30px 0;">
                        <h1 style="color: #e74c3c; font-size: 48px; letter-spacing: 10px; font-weight: bold;">%s</h1>
                    </div>
                    
                    <p>Vui lòng đăng nhập bằng mật khẩu này và thay đổi mật khẩu ngay lập tức để bảo mật tài khoản.</p>
                    <p>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.</p>
                    
                    <hr style="margin: 20px 0;">
                    <p style="color: #7f8c8d; font-size: 14px;">Trân trọng,<br><strong>Unipart Team</strong></p>
                </div>
                """.formatted(newPassword);

            helper.setText(htmlContent, true);
            helper.setFrom(fromEmail, "Unipart");   // ← Thay bằng email của bạn

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Không thể gửi email mật khẩu mới: " + e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_INVALID));
        String newRandomPassword = generateRandomPassword(8);

        String encodedPassword = passwordEncoder.encode(newRandomPassword);
        user.setPasswordHash(encodedPassword);
        userRepository.save(user);
        sendNewPasswordEmail(user.getEmail(), newRandomPassword);
    }
    public UserResponse changePassword(ChangePasswordRequest request) {
        String username = getCurrentUser().getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }
    @PreAuthorize("hasRole('STUDENT')")
    public StudentResponse updateProfileStudent(StudentUpdateRequest request) {
        User user = getCurrentUser();
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            if (userRepository.existsByPhoneNumberAndIdNot(request.getPhoneNumber(), user.getId())) {
                throw new AppException(ErrorCode.EXIST_PHONE);
            }
        }
        userMapper.updateUserFromRequest(request, user);
        userRepository.save(user);

        var student = studentRepository.findByUser(user);
        studentMapper.updateStudentFromRequest(request, student);
        studentRepository.save(student);

        return studentMapper.toStudentResponse(student);
    }


    public EmployerResponse registerEmployer(EmployerRegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())){
            throw new AppException(ErrorCode.EXIST_PHONE);
        }
        User user = employerMapper.toUserEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setIsActived(false); // Đợi xác thực OTP
        user.setIsBlocked(false);

        Role role = roleRepository.findByName("EMPLOYER")
                .orElseThrow();
        user.setRole(role);

        user = userRepository.save(user);
        otpService.generateAndSendOtp(new SendOTPRequest(request.getEmail()));
        Employer employer = employerMapper.toEmployerEntity(request);
        employer.setUser(user);
        employerRepository.save(employer);
        return employerMapper.toEmployerResponse(employer);
    }
    public EmployerResponse updateProfileEmployer(EmployerUpdateRequest request) {
        User user = getCurrentUser();
        Employer employer = employerRepository.findByUser(user);
        if (employer == null) {
            throw new RuntimeException("Không tìm thấy thông tin nhà tuyển dụng");
        }
        // Chỉ kiểm tra trùng SĐT nếu có nhập và khác SĐT hiện tại
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            if (userRepository.existsByPhoneNumberAndIdNot(request.getPhoneNumber(), user.getId())) {
                throw new AppException(ErrorCode.EXIST_PHONE);
            }
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            User uEmail = userRepository.findByEmail(request.getEmail()).orElse(null);
            if (uEmail != null && !uEmail.getId().equals(user.getId())) {
                throw new AppException(ErrorCode.EMAIL_EXISTED);
            }
        }
        employerMapper.updateUserFromRequest(request, user);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        employerMapper.updateEmployerFromRequest(request, employer);
        employerRepository.save(employer);
        return employerMapper.toEmployerResponse(employer);
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
        User user = getCurrentUser();
        Student student = user.getStudent();
        return studentMapper.toStudentResponse(student);
    }

    @PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
    public StudentResponse getStudentById(String id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        Student student = user.getStudent();
        return studentMapper.toStudentResponse(student);
    }
    @PreAuthorize("hasRole('EMPLOYER')")
    public EmployerResponse getEmployerMyInfo(){
        User user = getCurrentUser();
        var employer = employerRepository.findByUser(user);
        EmployerResponse response = employerMapper.toEmployerResponse(employer);

        java.util.List<EmployerPostQuota> quotas = employerPostQuotaRepository.findAllByEmployerId(employer.getId());
        
        int totalPosts = quotas.stream()
                .filter(q -> "NORMAL".equals(q.getQuotaType()) && ("TIN".equals(q.getType()) || (q.getType() == null && q.getExpiresAt() == null)))
                .mapToInt(EmployerPostQuota::getRemainingPosts)
                .sum();
        response.setRemainingPosts(totalPosts);
        
        int urgentPosts = quotas.stream()
                .filter(q -> "URGENT".equals(q.getQuotaType()) && ("TIN".equals(q.getType()) || (q.getType() == null && q.getExpiresAt() == null)))
                .mapToInt(EmployerPostQuota::getRemainingPosts)
                .sum();
        response.setRemainingUrgentPosts(urgentPosts);

        int monthlyNormalPosts = quotas.stream()
                .filter(q -> "NORMAL".equals(q.getQuotaType()) && ("MONTHLY".equals(q.getType()) || (q.getType() == null && q.getExpiresAt() != null)))
                .mapToInt(q -> {
                    if (q.getMaxPostsPerDay() != null) {
                        java.time.LocalDate today = java.time.LocalDate.now();
                        int used = (q.getLastResetDate() != null && q.getLastResetDate().equals(today) && q.getUsedPostsToday() != null) ? q.getUsedPostsToday() : 0;
                        return Math.max(0, Math.min(q.getMaxPostsPerDay() - used, q.getRemainingPosts()));
                    }
                    return q.getRemainingPosts();
                })
                .sum();
        response.setRemainingMonthlyPosts(monthlyNormalPosts);

        int monthlyUrgentPosts = quotas.stream()
                .filter(q -> "URGENT".equals(q.getQuotaType()) && ("MONTHLY".equals(q.getType()) || (q.getType() == null && q.getExpiresAt() != null)))
                .mapToInt(EmployerPostQuota::getRemainingPosts)
                .sum();
        response.setRemainingMonthlyUrgentPosts(monthlyUrgentPosts);

        response.setMonthlyMaxPostsPerDay(null); // No longer needed


        List<EmployerPackagePurchase> purchases = employerPackagePurchaseRepository.findAllByEmployerId(employer.getId());
        if (purchases != null && !purchases.isEmpty()) {
            // Lấy purchase gần nhất theo ID hoặc thời gian
            purchases.sort((a, b) -> b.getPurchasedAt().compareTo(a.getPurchasedAt()));
            EmployerPackagePurchase latestPurchase = purchases.get(0);
            if (latestPurchase.getSubscriptionPackage() != null) {
                response.setCurrentPackage(latestPurchase.getSubscriptionPackage().getName());
            } else {
                response.setCurrentPackage("Gói Cơ bản");
            }
        } else {
            response.setCurrentPackage("Gói Cơ bản");
        }

        return response;
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    public void blockUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        user.setIsBlocked(true);
        userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void unblockUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        user.setIsBlocked(false);
        userRepository.save(user);
    }

    public String getEmailByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        return user.getEmail();
    }
}
