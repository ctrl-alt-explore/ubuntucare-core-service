package za.pulsewatch.dto;

import za.pulsewatch.model.HealthMetrics;

import java.time.LocalDateTime;

public class PPGAnalysisResponse {
    private String sessionId;
    private String userId;
    private String deviceId;
    private HealthMetrics metrics;
    private String status;
    private String message;
    private LocalDateTime timestamp;
    private long processingTimeMs;
    private GamificationResponse gamificationResponse;

    public PPGAnalysisResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public PPGAnalysisResponse(String sessionId, String userId, String deviceId) {
        this();
        this.sessionId = sessionId;
        this.userId = userId;
        this.deviceId = deviceId;
    }

    // Getters and Setters
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    
    public HealthMetrics getMetrics() { return metrics; }
    public void setMetrics(HealthMetrics metrics) { this.metrics = metrics; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }

    public GamificationResponse getGamificationResponse() {
        return gamificationResponse;
    }

    public void setGamificationResponse(GamificationResponse gamificationResponse) {
        this.gamificationResponse = gamificationResponse;
    }
}
