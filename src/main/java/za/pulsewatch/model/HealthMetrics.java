package za.pulsewatch.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthMetrics {
    private String id;
    private String userId;
    private Double heartRate; // BPM
    private Double spo2; // Blood oxygen saturation (%)
    private Double hrv; // Heart rate variability (ms)
    private Double stressLevel; // 0-100 scale
    private Boolean irregularHeartbeat; // true if arrhythmia detected
    private String signalQuality; // "excellent", "good", "fair", "poor"
    private LocalDateTime timestamp;
    private String deviceId;
    
    public boolean isValid() {
        return heartRate != null && heartRate > 30 && heartRate < 250 &&
               spo2 != null && spo2 >= 70 && spo2 <= 100 &&
               hrv != null && hrv >= 10 && hrv <= 500;
    }
}
