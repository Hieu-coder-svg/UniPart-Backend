package com.unipart.unipart_backend.service.ServiceImpl;

import com.unipart.unipart_backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username:v4farmpixels@gmail.com}")
    private String fromEmail;

    @Value("${app.mail.from-name:UniPart}")
    private String fromName;

    private void sendHtmlEmail(String toEmail, String subject, String templateName, Context context) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject(subject);

            String htmlContent = templateEngine.process("email/" + templateName, context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email sent successfully to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error sending email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Override
    @Async
    public void sendReportRejectedEmail(String toEmail, String reporterName, Long reportId,
                                        String targetType, String adminNote) {
        Context context = new Context();
        context.setVariable("reporterName", reporterName);
        context.setVariable("reportId", reportId);
        context.setVariable("targetType", targetType);
        context.setVariable("adminNote", adminNote);

        sendHtmlEmail(toEmail, "📋 Thông báo: Báo cáo #" + reportId + " của bạn đã bị từ chối - UniPart", "report-rejected", context);
    }

    @Override
    @Async
    public void sendReportResolvedEmailToReporter(String toEmail, String reporterName, Long reportId, String targetType, String adminNote) {
        Context context = new Context();
        context.setVariable("reporterName", reporterName);
        context.setVariable("reportId", reportId);
        context.setVariable("targetType", targetType);
        context.setVariable("adminNote", adminNote);

        sendHtmlEmail(toEmail, "✅ Thông báo: Báo cáo #" + reportId + " đã được giải quyết - UniPart", "report-resolved", context);
    }

    @Override
    @Async
    public void sendReportResolvedEmailToOffender(String toEmail, String offenderName, Long reportId, String targetType, String adminNote) {
        Context context = new Context();
        context.setVariable("offenderName", offenderName);
        context.setVariable("adminNote", adminNote);

        sendHtmlEmail(toEmail, "⚠️ Thông báo: Bạn có một báo cáo vi phạm cần lưu ý - UniPart", "report-resolved-offender", context);
    }

    @Override
    @Async
    public void sendApplicationAcceptedEmail(String toEmail, String studentName, String jobTitle, String companyName) {
        Context context = new Context();
        context.setVariable("studentName", studentName);
        context.setVariable("jobTitle", jobTitle);
        context.setVariable("companyName", companyName);

        sendHtmlEmail(toEmail, "🎉 Chúc mừng! Bạn đã trúng tuyển công việc " + jobTitle + " - UniPart", "application-accepted", context);
    }

    @Override
    @Async
    public void sendApplicationRejectedEmail(String toEmail, String studentName, String jobTitle, String companyName) {
        Context context = new Context();
        context.setVariable("studentName", studentName);
        context.setVariable("jobTitle", jobTitle);
        context.setVariable("companyName", companyName);

        sendHtmlEmail(toEmail, "Thông báo kết quả ứng tuyển - UniPart", "application-rejected", context);
    }

    @Override
    @Async
    public void sendApplicationCompletedEmail(String toEmail, String studentName, String jobTitle, String companyName) {
        Context context = new Context();
        context.setVariable("studentName", studentName);
        context.setVariable("jobTitle", jobTitle);
        context.setVariable("companyName", companyName);

        sendHtmlEmail(toEmail, "⭐ Chúc mừng! Bạn đã hoàn thành công việc - UniPart", "application-completed", context);
    }

    @Override
    @Async
    public void sendReviewReceivedEmailToEmployer(String toEmail, String employerName, String studentName, String jobTitle, int rating, String comment) {
        Context context = new Context();
        context.setVariable("recipientName", employerName);
        context.setVariable("senderRole", "Ứng viên");
        context.setVariable("senderName", studentName);
        context.setVariable("jobTitle", jobTitle);
        context.setVariable("rating", rating);
        context.setVariable("comment", comment);

        sendHtmlEmail(toEmail, "⭐ Bạn có một đánh giá mới - UniPart", "review-received", context);
    }

    @Override
    @Async
    public void sendReviewReceivedEmailToStudent(String toEmail, String studentName, String employerName, String jobTitle, int rating, String comment) {
        Context context = new Context();
        context.setVariable("recipientName", studentName);
        context.setVariable("senderRole", "Nhà tuyển dụng");
        context.setVariable("senderName", employerName);
        context.setVariable("jobTitle", jobTitle);
        context.setVariable("rating", rating);
        context.setVariable("comment", comment);

        sendHtmlEmail(toEmail, "⭐ Bạn có một đánh giá mới - UniPart", "review-received", context);
    }

    @Override
    @Async
    public void sendAccountBannedEmail(String toEmail, String fullName, String reason) {
        Context context = new Context();
        context.setVariable("fullName", fullName);
        context.setVariable("reason", reason);

        sendHtmlEmail(toEmail, "🚨 THÔNG BÁO: Tài khoản của bạn đã bị khóa", "account-banned", context);
    }

    @Override
    @Async
    public void sendAccountUnbannedEmail(String toEmail, String fullName) {
        Context context = new Context();
        context.setVariable("fullName", fullName);

        sendHtmlEmail(toEmail, "✅ THÔNG BÁO: Tài khoản của bạn đã được khôi phục", "account-unbanned", context);
    }
}
