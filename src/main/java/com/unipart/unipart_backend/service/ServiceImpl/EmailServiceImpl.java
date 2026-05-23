package com.unipart.unipart_backend.service.ServiceImpl;

import com.unipart.unipart_backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:v4farmpixels@gmail.com}")
    private String fromEmail;

    @Value("${app.mail.from-name:UniPart}")
    private String fromName;

    @Override
    @Async
    public void sendReportRejectedEmail(String toEmail, String reporterName, Long reportId,
                                        String targetType, String adminNote) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("📋 Thông báo: Báo cáo #" + reportId + " của bạn đã bị từ chối - UniPart");

            String htmlContent = buildReportRejectedEmailContent(reporterName, reportId, targetType, adminNote);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Report rejected email sent successfully to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send report rejected email to {}: {}", toEmail, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error sending report rejected email to {}: {}", toEmail, e.getMessage());
        }
    }

    private String buildReportRejectedEmailContent(String reporterName, Long reportId,
                                                    String targetType, String adminNote) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html lang=\"vi\">");
        html.append("<head>");
        html.append("<meta charset=\"UTF-8\">");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        html.append("<title>Thông báo báo cáo bị từ chối</title>");
        html.append("</head>");
        html.append("<body style=\"font-family: 'Segoe UI', Arial, sans-serif; background-color: #f5f5f5; margin: 0; padding: 20px;\">");

        // Container
        html.append("<div style=\"max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 20px rgba(0,0,0,0.1);\">");

        // Header
        html.append("<div style=\"background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%); padding: 30px; text-align: center;\">");
        html.append("<h1 style=\"color: #ffffff; margin: 0; font-size: 24px; font-weight: 600;\">");
        html.append("⚠️ Báo cáo của bạn đã bị từ chối");
        html.append("</h1>");
        html.append("<p style=\"color: rgba(255,255,255,0.9); margin: 10px 0 0 0; font-size: 14px;\">");
        html.append("UniPart - Nền tảng kết nối việc làm Part-time");
        html.append("</p>");
        html.append("</div>");

        // Content
        html.append("<div style=\"padding: 30px;\">");

        // Greeting
        html.append("<p style=\"font-size: 16px; color: #333333; margin: 0 0 20px 0;\">");
        html.append("Xin chào <strong>").append(reporterName).append("</strong>,");
        html.append("</p>");

        // Main message
        html.append("<p style=\"font-size: 15px; color: #555555; line-height: 1.6; margin: 0 0 20px 0;\">");
        html.append("Chúng tôi xin thông báo rằng báo cáo vi phạm #<strong>").append(reportId).append("</strong> ");
        html.append("của bạn về đối tượng <strong>").append(targetType).append("</strong> đã được Quản trị viên xem xét và <span style=\"color: #ef4444; font-weight: 600;\">bị từ chối</span>.");
        html.append("</p>");

        // Report details card
        html.append("<div style=\"background: #fef2f2; border: 1px solid #fecaca; border-radius: 12px; padding: 20px; margin: 0 0 20px 0;\">");
        html.append("<h3 style=\"color: #dc2626; margin: 0 0 15px 0; font-size: 16px;\">📋 Chi tiết báo cáo</h3>");
        html.append("<table style=\"width: 100%; font-size: 14px;\">");
        html.append("<tr>");
        html.append("<td style=\"padding: 8px 0; color: #666666;\">Mã báo cáo:</td>");
        html.append("<td style=\"padding: 8px 0; color: #333333; font-weight: 600;\">#").append(reportId).append("</td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<td style=\"padding: 8px 0; color: #666666;\">Đối tượng báo cáo:</td>");
        html.append("<td style=\"padding: 8px 0; color: #333333; font-weight: 600;\">").append(targetType).append("</td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<td style=\"padding: 8px 0; color: #666666;\">Trạng thái:</td>");
        html.append("<td style=\"padding: 8px 0;\">");
        html.append("<span style=\"background: #d1d5db; color: #374151; padding: 4px 12px; border-radius: 20px; font-size: 12px; font-weight: 600;\">");
        html.append("BỊ TỪ CHỐI");
        html.append("</span>");
        html.append("</td>");
        html.append("</tr>");
        html.append("</table>");
        html.append("</div>");

        // Admin note section
        if (adminNote != null && !adminNote.trim().isEmpty()) {
            html.append("<div style=\"background: #fffbeb; border: 1px solid #fcd34d; border-radius: 12px; padding: 20px; margin: 0 0 20px 0;\">");
            html.append("<h3 style=\"color: #b45309; margin: 0 0 10px 0; font-size: 16px;\">📝 Ghi chú từ Quản trị viên</h3>");
            html.append("<p style=\"font-size: 15px; color: #78350f; margin: 0; line-height: 1.6; font-style: italic;\">");
            html.append("\"").append(adminNote).append("\"");
            html.append("</p>");
            html.append("</div>");
        }

        // Explanation
        html.append("<div style=\"background: #f3f4f6; border-radius: 12px; padding: 20px; margin: 0 0 20px 0;\">");
        html.append("<p style=\"font-size: 14px; color: #555555; margin: 0; line-height: 1.6;\">");
        html.append("Quyết định này được đưa ra sau khi Quản trị viên xem xét kỹ lưỡng nội dung báo cáo và các bằng chứng liên quan. ");
        html.append("Nếu bạn không đồng ý với quyết định này hoặc có thông tin bổ sung, vui lòng liên hệ với bộ phận hỗ trợ của chúng tôi.");
        html.append("</p>");
        html.append("</div>");

        // Contact info
        html.append("<div style=\"text-align: center; padding: 20px;\">");
        html.append("<p style=\"font-size: 14px; color: #666666; margin: 0;\">");
        html.append("Cảm ơn bạn đã đồng hành cùng UniPart!");
        html.append("</p>");
        html.append("<p style=\"font-size: 12px; color: #999999; margin: 10px 0 0 0;\">");
        html.append("Email này được gửi tự động từ hệ thống UniPart. Vui lòng không trả lời email này.");
        html.append("</p>");
        html.append("</div>");

        html.append("</div>"); // end content

        // Footer
        html.append("<div style=\"background: #1f2937; padding: 20px; text-align: center;\">");
        html.append("<p style=\"color: #9ca3af; font-size: 12px; margin: 0;\">");
        html.append("© 2024 UniPart. Mọi quyền được bảo lưu.");
        html.append("</p>");
        html.append("</div>");

        html.append("</div>"); // end container
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }
    @Override
    @Async
    public void sendReportResolvedEmailToReporter(String toEmail, String reporterName, Long reportId, String targetType, String adminNote) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("✅ Thông báo: Báo cáo #" + reportId + " đã được giải quyết - UniPart");

            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html><html lang=\"vi\"><head><meta charset=\"UTF-8\"></head><body style=\"font-family: Arial; padding: 20px;\">");
            html.append("<div style=\"max-width: 600px; margin: 0 auto; border: 1px solid #ddd; border-radius: 8px; overflow: hidden;\">");
            html.append("<div style=\"background: #10b981; color: white; padding: 20px; text-align: center;\"><h2>✅ Báo cáo đã được giải quyết</h2></div>");
            html.append("<div style=\"padding: 20px;\"><p>Xin chào <strong>").append(reporterName).append("</strong>,</p>");
            html.append("<p>Báo cáo #<strong>").append(reportId).append("</strong> của bạn về <strong>").append(targetType).append("</strong> đã được Quản trị viên xử lý thành công.</p>");
            if (adminNote != null && !adminNote.isEmpty()) {
                html.append("<div style=\"background: #f0fdf4; padding: 15px; border-left: 4px solid #16a34a;\"><p><strong>Ghi chú từ QTV:</strong><br>").append(adminNote).append("</p></div>");
            }
            html.append("<p>Cảm ơn bạn đã góp phần xây dựng cộng đồng UniPart vững mạnh.</p></div></div></body></html>");

            helper.setText(html.toString(), true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send email: " + e.getMessage());
        }
    }

    @Override
    @Async
    public void sendReportResolvedEmailToOffender(String toEmail, String offenderName, Long reportId, String targetType, String adminNote) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("⚠️ Thông báo: Bạn có một báo cáo vi phạm cần lưu ý - UniPart");

            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html><html lang=\"vi\"><head><meta charset=\"UTF-8\"></head><body style=\"font-family: Arial; padding: 20px;\">");
            html.append("<div style=\"max-width: 600px; margin: 0 auto; border: 1px solid #ddd; border-radius: 8px; overflow: hidden;\">");
            html.append("<div style=\"background: #f59e0b; color: white; padding: 20px; text-align: center;\"><h2>⚠️ Báo cáo vi phạm</h2></div>");
            html.append("<div style=\"padding: 20px;\"><p>Xin chào <strong>").append(offenderName).append("</strong>,</p>");
            html.append("<p>Chúng tôi nhận được báo cáo về tài khoản/bài viết của bạn và đã tiến hành xem xét.</p>");
            if (adminNote != null && !adminNote.isEmpty()) {
                html.append("<div style=\"background: #fffbeb; padding: 15px; border-left: 4px solid #d97706;\"><p><strong>Ghi chú từ QTV:</strong><br>").append(adminNote).append("</p></div>");
            }
            html.append("<p>Vui lòng tuân thủ các tiêu chuẩn cộng đồng của UniPart để tránh việc tài khoản bị hạn chế.</p></div></div></body></html>");

            helper.setText(html.toString(), true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send email: " + e.getMessage());
        }
    }

    @Override
    @Async
    public void sendApplicationAcceptedEmail(String toEmail, String studentName, String jobTitle, String companyName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("🎉 Chúc mừng! Bạn đã trúng tuyển công việc " + jobTitle + " - UniPart");

            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html><html lang=\"vi\"><head><meta charset=\"UTF-8\"></head><body style=\"font-family: Arial; padding: 20px;\">");
            html.append("<div style=\"max-width: 600px; margin: 0 auto; border: 1px solid #ddd; border-radius: 8px; overflow: hidden;\">");
            html.append("<div style=\"background: #3b82f6; color: white; padding: 20px; text-align: center;\"><h2>🎉 Chúc mừng bạn trúng tuyển</h2></div>");
            html.append("<div style=\"padding: 20px;\"><p>Xin chào <strong>").append(studentName).append("</strong>,</p>");
            html.append("<p>Tin vui! Nhà tuyển dụng <strong>").append(companyName).append("</strong> đã chấp nhận đơn ứng tuyển của bạn cho vị trí <strong>").append(jobTitle).append("</strong>.</p>");
            html.append("<p>Vui lòng kiểm tra lại thông tin công việc và chủ động liên hệ với nhà tuyển dụng để trao đổi cụ thể hơn nhé.</p></div></div></body></html>");

            helper.setText(html.toString(), true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send email: " + e.getMessage());
        }
    }

    @Override
    @Async
    public void sendApplicationRejectedEmail(String toEmail, String studentName, String jobTitle, String companyName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("Thông báo kết quả ứng tuyển - UniPart");

            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html><html lang=\"vi\"><head><meta charset=\"UTF-8\"></head><body style=\"font-family: Arial; padding: 20px;\">");
            html.append("<div style=\"max-width: 600px; margin: 0 auto; border: 1px solid #ddd; border-radius: 8px; overflow: hidden;\">");
            html.append("<div style=\"background: #6b7280; color: white; padding: 20px; text-align: center;\"><h2>Thông báo kết quả ứng tuyển</h2></div>");
            html.append("<div style=\"padding: 20px;\"><p>Xin chào <strong>").append(studentName).append("</strong>,</p>");
            html.append("<p>Cảm ơn bạn đã ứng tuyển vị trí <strong>").append(jobTitle).append("</strong> tại <strong>").append(companyName).append("</strong>.</p>");
            html.append("<p>Rất tiếc, hồ sơ của bạn chưa phù hợp với vị trí này. Đừng nản lòng, còn rất nhiều cơ hội việc làm khác đang chờ bạn trên UniPart!</p></div></div></body></html>");

            helper.setText(html.toString(), true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send email: " + e.getMessage());
        }
    }

    @Override
    @Async
    public void sendApplicationCompletedEmail(String toEmail, String studentName, String jobTitle, String companyName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("⭐ Chúc mừng! Bạn đã hoàn thành công việc - UniPart");

            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html><html lang=\"vi\"><head><meta charset=\"UTF-8\"></head><body style=\"font-family: Arial; padding: 20px;\">");
            html.append("<div style=\"max-width: 600px; margin: 0 auto; border: 1px solid #ddd; border-radius: 8px; overflow: hidden;\">");
            html.append("<div style=\"background: #3b82f6; color: white; padding: 20px; text-align: center;\"><h2>⭐ Hoàn thành công việc</h2></div>");
            html.append("<div style=\"padding: 20px;\"><p>Xin chào <strong>").append(studentName).append("</strong>,</p>");
            html.append("<p>Tuyệt vời! Nhà tuyển dụng <strong>").append(companyName).append("</strong> đã xác nhận bạn hoàn thành công việc <strong>").append(jobTitle).append("</strong>.</p>");
            html.append("<p>Giờ đây bạn đã có thể để lại đánh giá cho nhà tuyển dụng trên hệ thống.</p></div></div></body></html>");

            helper.setText(html.toString(), true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send email: " + e.getMessage());
        }
    }

    @Override
    @Async
    public void sendReviewReceivedEmailToEmployer(String toEmail, String employerName, String studentName, String jobTitle, int rating, String comment) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("⭐ Bạn có một đánh giá mới - UniPart");

            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html><html lang=\"vi\"><head><meta charset=\"UTF-8\"></head><body style=\"font-family: Arial; padding: 20px;\">");
            html.append("<div style=\"max-width: 600px; margin: 0 auto; border: 1px solid #ddd; border-radius: 8px; overflow: hidden;\">");
            html.append("<div style=\"background: #f59e0b; color: white; padding: 20px; text-align: center;\"><h2>⭐ Đánh giá mới</h2></div>");
            html.append("<div style=\"padding: 20px;\"><p>Xin chào <strong>").append(employerName).append("</strong>,</p>");
            html.append("<p>Ứng viên <strong>").append(studentName).append("</strong> đã để lại đánh giá cho bạn về công việc <strong>").append(jobTitle).append("</strong>.</p>");
            html.append("<p><strong>Đánh giá:</strong> ").append(rating).append(" sao</p>");
            if (comment != null && !comment.isEmpty()) {
                html.append("<p><strong>Nhận xét:</strong> ").append(comment).append("</p>");
            }
            html.append("</div></div></body></html>");

            helper.setText(html.toString(), true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send email: " + e.getMessage());
        }
    }

    @Override
    @Async
    public void sendReviewReceivedEmailToStudent(String toEmail, String studentName, String employerName, String jobTitle, int rating, String comment) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("⭐ Bạn có một đánh giá mới - UniPart");

            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html><html lang=\"vi\"><head><meta charset=\"UTF-8\"></head><body style=\"font-family: Arial; padding: 20px;\">");
            html.append("<div style=\"max-width: 600px; margin: 0 auto; border: 1px solid #ddd; border-radius: 8px; overflow: hidden;\">");
            html.append("<div style=\"background: #f59e0b; color: white; padding: 20px; text-align: center;\"><h2>⭐ Đánh giá mới</h2></div>");
            html.append("<div style=\"padding: 20px;\"><p>Xin chào <strong>").append(studentName).append("</strong>,</p>");
            html.append("<p>Nhà tuyển dụng <strong>").append(employerName).append("</strong> đã để lại đánh giá cho bạn về công việc <strong>").append(jobTitle).append("</strong>.</p>");
            html.append("<p><strong>Đánh giá:</strong> ").append(rating).append(" sao</p>");
            if (comment != null && !comment.isEmpty()) {
                html.append("<p><strong>Nhận xét:</strong> ").append(comment).append("</p>");
            }
            html.append("</div></div></body></html>");

            helper.setText(html.toString(), true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send email: " + e.getMessage());
        }
    }

    @Override
    @Async
    public void sendAccountBannedEmail(String toEmail, String fullName, String reason) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("🚨 THÔNG BÁO: Tài khoản của bạn đã bị khóa");

            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html><html lang=\"vi\"><head><meta charset=\"UTF-8\"></head><body style=\"font-family: Arial; padding: 20px;\">");
            html.append("<div style=\"max-width: 600px; margin: 0 auto; border: 1px solid #ddd; border-radius: 8px; overflow: hidden;\">");
            html.append("<div style=\"background: #ef4444; color: white; padding: 20px; text-align: center;\"><h2>🚨 TÀI KHOẢN BỊ KHÓA</h2></div>");
            html.append("<div style=\"padding: 20px;\"><p>Xin chào <strong>").append(fullName).append("</strong>,</p>");
            html.append("<p>Chúng tôi rất tiếc phải thông báo rằng tài khoản của bạn đã bị khóa.</p>");
            html.append("<div style=\"background: #fef2f2; padding: 15px; border-left: 4px solid #ef4444;\"><p><strong>Lý do:</strong><br>").append(reason).append("</p></div>");
            html.append("<p>Nếu bạn cho rằng quyết định này là nhầm lẫn, vui lòng liên hệ bộ phận hỗ trợ.</p></div></div></body></html>");

            helper.setText(html.toString(), true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send email: " + e.getMessage());
        }
    }

    @Override
    @Async
    public void sendAccountUnbannedEmail(String toEmail, String fullName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("✅ THÔNG BÁO: Tài khoản của bạn đã được khôi phục");

            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html><html lang=\"vi\"><head><meta charset=\"UTF-8\"></head><body style=\"font-family: Arial; padding: 20px;\">");
            html.append("<div style=\"max-width: 600px; margin: 0 auto; border: 1px solid #ddd; border-radius: 8px; overflow: hidden;\">");
            html.append("<div style=\"background: #10b981; color: white; padding: 20px; text-align: center;\"><h2>✅ TÀI KHOẢN ĐÃ ĐƯỢC MỞ KHÓA</h2></div>");
            html.append("<div style=\"padding: 20px;\"><p>Xin chào <strong>").append(fullName).append("</strong>,</p>");
            html.append("<p>Tài khoản của bạn đã được khôi phục và mở khóa thành công. Bạn hiện có thể đăng nhập và sử dụng dịch vụ bình thường.</p>");
            html.append("</div></div></body></html>");

            helper.setText(html.toString(), true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send email: " + e.getMessage());
        }
    }
}
