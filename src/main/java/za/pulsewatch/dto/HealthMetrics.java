package za.pulsewatch.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HealthMetrics {
    private Double heartRate;
    private Double bloodOxygen;
    private Double heartRateVariability;
    private Double stressLevel;
    private Boolean arrhythmiaDetected;
    private Double signalQuality;
    private LocalDateTime timestamp;
    private String deviceId;
    private String userId;
    private Integer healthScore;
    private Boolean offlineMode;
    
    // Constructors
    public HealthMetrics() {
        this.timestamp = LocalDateTime.now();
    }
    
    public HealthMetrics(String userId, String deviceId) {
        this();
        this.userId = userId;
        this.deviceId = deviceId;
    }
    
    // Getters and Setters
    public Double getHeartRate() { return heartRate; }
    public void setHeartRate(Double heartRate) { this.heartRate = heartRate; }
    
    public Double getBloodOxygen() { return bloodOxygen; }
    public void setBloodOxygen(Double bloodOxygen) { this.bloodOxygen = bloodOxygen; }
    
    public Double getHeartRateVariability() { return heartRateVariability; }
    public void setHeartRateVariability(Double heartRateVariability) { this.heartRateVariability = heartRateVariability; }
    
    public Double getStressLevel() { return stressLevel; }
    public void setStressLevel(Double stressLevel) { this.stressLevel = stressLevel; }
    
    public Boolean getArrhythmiaDetected() { return arrhythmiaDetected; }
    public void setArrhythmiaDetected(Boolean arrhythmiaDetected) { this.arrhythmiaDetected = arrhythmiaDetected; }
    
    public Double getSignalQuality() { return signalQuality; }
    public void setSignalQuality(Double signalQuality) { this.signalQuality = signalQuality; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public Integer getHealthScore() { return healthScore; }
    public void setHealthScore(Integer healthScore) { this.healthScore = healthScore; }
    
    public Boolean getOfflineMode() { return offlineMode; }
    public void setOfflineMode(Boolean offlineMode) { this.offlineMode = offlineMode; }
    
    @Override
    public String toString() {
        return "HealthMetrics{" +
                "heartRate=" + heartRate +
                ", bloodOxygen=" + bloodOxygen +
                ", heartRateVariability=" + heartRateVariability +
                ", stressLevel=" + stressLevel +
                ", arrhythmiaDetected=" + arrhythmiaDetected +
                ", signalQuality=" + signalQuality +
                ", timestamp=" + timestamp +
                ", deviceId='" + deviceId + '\'' +
                ", userId='" + userId + '\'' +
                ", healthScore=" + healthScore +
                ", offlineMode=" + offlineMode +
                '}';
    }
}
