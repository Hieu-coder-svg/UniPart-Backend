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
}
