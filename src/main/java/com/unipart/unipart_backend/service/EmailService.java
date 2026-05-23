package com.unipart.unipart_backend.service;

public interface EmailService {
    void sendReportRejectedEmail(String toEmail, String reporterName, Long reportId,
                                 String targetType, String adminNote);

    void sendReportResolvedEmailToReporter(String toEmail, String reporterName, Long reportId,
                                           String targetType, String adminNote);

    void sendReportResolvedEmailToOffender(String toEmail, String offenderName, Long reportId,
                                           String targetType, String adminNote);

    void sendApplicationAcceptedEmail(String toEmail, String studentName, String jobTitle, String companyName);

    void sendApplicationRejectedEmail(String toEmail, String studentName, String jobTitle, String companyName);

    void sendApplicationCompletedEmail(String toEmail, String studentName, String jobTitle, String companyName);

    void sendReviewReceivedEmailToEmployer(String toEmail, String employerName, String studentName, String jobTitle, int rating, String comment);

    void sendReviewReceivedEmailToStudent(String toEmail, String studentName, String employerName, String jobTitle, int rating, String comment);

    void sendAccountBannedEmail(String toEmail, String fullName, String reason);

    void sendAccountUnbannedEmail(String toEmail, String fullName);
}
