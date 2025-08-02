package za.pulsewatch.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PPGSignal {
    private String id;
    private List<Double> rawData;
    private double samplingRate; // Hz
    private LocalDateTime timestamp;
    private String deviceId;
    private String userId;
    
    public int getSampleCount() {
        return rawData != null ? rawData.size() : 0;
    }
    
    public double getDuration() {
        return rawData != null ? rawData.size() / samplingRate : 0.0;
    }
}
