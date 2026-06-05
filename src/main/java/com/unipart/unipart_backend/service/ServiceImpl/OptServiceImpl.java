package com.unipart.unipart_backend.service.ServiceImpl;

import com.unipart.unipart_backend.dto.request.SendOTPRequest;
import com.unipart.unipart_backend.dto.request.VerifyOTPRequest;
import com.unipart.unipart_backend.entity.Otp;
import com.unipart.unipart_backend.entity.User;
import com.unipart.unipart_backend.exception.AppException;
import com.unipart.unipart_backend.exception.ErrorCode;
import com.unipart.unipart_backend.repository.OtpRepository;
import com.unipart.unipart_backend.repository.UserRepository;
import com.unipart.unipart_backend.service.OtpService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Random;
@Service
@RequiredArgsConstructor
public class OptServiceImpl implements OtpService {
    private final OtpRepository otpRepository;
    private final JavaMailSender mailSender;
   private final UserRepository userRepository;
    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final int OTP_EXPIRATION_MINUTES = 5;
    @Transactional
    public void generateAndSendOtp(SendOTPRequest request) {

        otpRepository.deleteOtpByEmail(request.getEmail());
        Integer otpCode = generateOtpCode();
        Otp otp = Otp.builder()
                .email(request.getEmail())
                .otpCode(otpCode)
                .expirationTime(LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES))
                .isUsed(false)
                .createdAt(LocalDateTime.now())
                .build();
        otpRepository.save(otp);

        sendOtpEmail(request.getEmail(), otpCode);
    }

    @Transactional
    public void verifyOtp(VerifyOTPRequest request) {
        Otp otp = otpRepository.findFirstByEmailAndIsUsedOrderByCreatedAtDesc(request.getEmail(), false)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_OTP));
        Integer inputOtpCode;
        try {
            inputOtpCode = Integer.parseInt(request.getOtp());
        } catch (NumberFormatException e) {
            throw new AppException(ErrorCode.INVALID_OTP_FORMAT);
        }
        if (!otp.getOtpCode().equals(inputOtpCode)) {
            throw new AppException(ErrorCode.WRONG_OTP);
        }
        if (otp.isExpired()) {
            throw new AppException(ErrorCode.EXPIRED_OTP);
        }
        otp.setIsUsed(true);
        otpRepository.save(otp);
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(()->new AppException(ErrorCode.EMAIL_INVALID));
        user.setIsActived(true);
        userRepository.save(user);
    }
    private void sendOtpEmail(String toEmail, Integer otpCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject(" Mã OTP xác thực tài khoản UniHire");

            String htmlContent = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;">
                    <h2 style="color: #2c3e50;">Xác thực tài khoản UniHire</h2>
                    <p>Xin chào,</p>
                    <p>Bạn đang đăng ký tài khoản <strong>Người dùng mới</strong>. Mã OTP của bạn là:</p>
                    
                    <div style="text-align: center; margin: 30px 0;">
                        <h1 style="color: #e74c3c; font-size: 48px; letter-spacing: 10px; font-weight: bold;">%d</h1>
                    </div>
                    
                    <p>Mã này sẽ <strong>hết hạn sau 5 phút</strong>.</p>
                    <p>Nếu bạn không yêu cầu, vui lòng bỏ qua email này.</p>
                    
                    <hr style="margin: 20px 0;">
                    <p style="color: #7f8c8d; font-size: 14px;">Trân trọng,<br><strong>UniHire Team</strong></p>
                </div>
                """.formatted(otpCode);

            helper.setText(htmlContent, true);
            helper.setFrom(fromEmail, "UniHire");

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Không thể gửi email OTP: " + e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private Integer generateOtpCode() {
        Random random = new Random();
        return 100000 + random.nextInt(900000);
    }
}
