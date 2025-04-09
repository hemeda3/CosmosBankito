package com.ahmedyousri.boilerplate.springboot.banking.monitoring.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Service for sending alerts.
 * This service provides methods for sending alerts to administrators and other stakeholders.
 */
@Service
@RequiredArgsConstructor
public class AlertService {
    
    private static final Logger log = LoggerFactory.getLogger(AlertService.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private final JavaMailSender mailSender;
    
    @Value("${app.alerts.email.enabled:false}")
    private boolean emailAlertsEnabled;
    
    @Value("${app.alerts.email.recipients:admin@bank.com}")
    private String alertEmailRecipients;
    
    @Value("${app.alerts.email.from:alerts@bank.com}")
    private String alertEmailFrom;
    
    @Value("${app.alerts.sms.enabled:false}")
    private boolean smsAlertsEnabled;
    
    @Value("${app.alerts.sms.recipients:+1234567890}")
    private String alertSmsRecipients;
    
    // Store recent alerts in memory for quick access
    private final ConcurrentLinkedQueue<Alert> recentAlerts = new ConcurrentLinkedQueue<>();
    private static final int MAX_RECENT_ALERTS = 100;
    
    /**
     * Send an alert.
     *
     * @param message The alert message
     */
    @Async
    public void sendAlert(String message) {
        sendAlert(message, AlertLevel.INFO);
    }
    
    /**
     * Send an alert with a specified level.
     *
     * @param message The alert message
     * @param level   The alert level
     */
    @Async
    public void sendAlert(String message, AlertLevel level) {
        log.info("Sending alert: {} (Level: {})", message, level);
        
        // Create alert object
        Alert alert = new Alert(message, level, LocalDateTime.now());
        
        // Add to recent alerts
        addToRecentAlerts(alert);
        
        // Send email alert if enabled
        if (emailAlertsEnabled && level.ordinal() >= AlertLevel.WARNING.ordinal()) {
            sendEmailAlert(alert);
        }
        
        // Send SMS alert if enabled and level is high enough
        if (smsAlertsEnabled && level.ordinal() >= AlertLevel.ERROR.ordinal()) {
            sendSmsAlert(alert);
        }
    }
    
    /**
     * Send an email alert.
     *
     * @param alert The alert to send
     */
    private void sendEmailAlert(Alert alert) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(alertEmailFrom);
            message.setTo(alertEmailRecipients.split(","));
            message.setSubject("Banking System Alert: " + alert.getLevel());
            message.setText(formatAlertMessage(alert));
            
            mailSender.send(message);
            
            log.debug("Email alert sent successfully");
        } catch (Exception e) {
            log.error("Error sending email alert: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Send an SMS alert.
     *
     * @param alert The alert to send
     */
    private void sendSmsAlert(Alert alert) {
        try {
            // Implementation would depend on the SMS service being used
            // This is a placeholder for the actual implementation
            log.info("SMS alert would be sent to {} with message: {}", alertSmsRecipients, formatAlertMessage(alert));
            
            log.debug("SMS alert sent successfully");
        } catch (Exception e) {
            log.error("Error sending SMS alert: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Format an alert message.
     *
     * @param alert The alert to format
     * @return The formatted alert message
     */
    private String formatAlertMessage(Alert alert) {
        return String.format("[%s] [%s] %s",
                alert.getTimestamp().format(DATE_TIME_FORMATTER),
                alert.getLevel(),
                alert.getMessage());
    }
    
    /**
     * Add an alert to the recent alerts queue.
     *
     * @param alert The alert to add
     */
    private void addToRecentAlerts(Alert alert) {
        recentAlerts.add(alert);
        
        // Trim the queue if it exceeds the maximum size
        while (recentAlerts.size() > MAX_RECENT_ALERTS) {
            recentAlerts.poll();
        }
    }
    
    /**
     * Get recent alerts.
     *
     * @return A list of recent alerts
     */
    public List<Alert> getRecentAlerts() {
        return new ArrayList<>(recentAlerts);
    }
    
    /**
     * Get recent alerts of a specific level or higher.
     *
     * @param level The minimum alert level
     * @return A list of recent alerts of the specified level or higher
     */
    public List<Alert> getRecentAlerts(AlertLevel level) {
        List<Alert> filteredAlerts = new ArrayList<>();
        
        for (Alert alert : recentAlerts) {
            if (alert.getLevel().ordinal() >= level.ordinal()) {
                filteredAlerts.add(alert);
            }
        }
        
        return filteredAlerts;
    }
    
    /**
     * Alert levels.
     */
    public enum AlertLevel {
        /**
         * Informational alert.
         */
        INFO,
        
        /**
         * Warning alert.
         */
        WARNING,
        
        /**
         * Error alert.
         */
        ERROR,
        
        /**
         * Critical alert.
         */
        CRITICAL
    }
    
    /**
     * Alert class.
     */
    public static class Alert {
        private final String message;
        private final AlertLevel level;
        private final LocalDateTime timestamp;
        
        /**
         * Construct a new alert.
         *
         * @param message   The alert message
         * @param level     The alert level
         * @param timestamp The alert timestamp
         */
        public Alert(String message, AlertLevel level, LocalDateTime timestamp) {
            this.message = message;
            this.level = level;
            this.timestamp = timestamp;
        }
        
        /**
         * Get the alert message.
         *
         * @return The alert message
         */
        public String getMessage() {
            return message;
        }
        
        /**
         * Get the alert level.
         *
         * @return The alert level
         */
        public AlertLevel getLevel() {
            return level;
        }
        
        /**
         * Get the alert timestamp.
         *
         * @return The alert timestamp
         */
        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
}
