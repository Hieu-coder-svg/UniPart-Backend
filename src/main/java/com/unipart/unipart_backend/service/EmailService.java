package com.unipart.unipart_backend.service;

public interface EmailService {
    void sendReportRejectedEmail(String toEmail, String reporterName, Long reportId, String targetType, String adminNote);
}
