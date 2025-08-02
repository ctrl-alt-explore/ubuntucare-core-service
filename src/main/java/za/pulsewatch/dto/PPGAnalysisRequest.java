package za.pulsewatch.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import java.util.UUID;

public class PPGAnalysisRequest {
    
    @NotNull(message = "User ID is required")
    private String userId;
    
    @NotNull(message = "Device ID is required")
    private String deviceId;
    
    @NotNull(message = "Raw data is required")
    private List<Double> rawData;
    
    @Positive(message = "Sampling rate must be positive")
    private double samplingRate;
    
    private String sessionId;
    
    public PPGAnalysisRequest() {
        this.sessionId = UUID.randomUUID().toString();
    }
    
    public PPGAnalysisRequest(String userId, String deviceId, List<Double> rawData, double samplingRate) {
        this();
        this.userId = userId;
        this.deviceId = deviceId;
        this.rawData = rawData;
        this.samplingRate = samplingRate;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    
    public List<Double> getRawData() { return rawData; }
    public void setRawData(List<Double> rawData) { this.rawData = rawData; }
    
    public double getSamplingRate() { return samplingRate; }
    public void setSamplingRate(double samplingRate) { this.samplingRate = samplingRate; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
}
