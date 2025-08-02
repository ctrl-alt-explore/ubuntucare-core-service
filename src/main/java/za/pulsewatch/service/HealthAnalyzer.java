package za.pulsewatch.service;

import org.springframework.stereotype.Service;
import za.pulsewatch.model.HealthMetrics;
import za.pulsewatch.model.PPGSignal;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HealthAnalyzer {
    
    private final SignalProcessor signalProcessor;
    
    public HealthAnalyzer(SignalProcessor signalProcessor) {
        this.signalProcessor = signalProcessor;
    }
    
    public HealthMetrics analyzePPGSignal(PPGSignal ppgSignal) {
        if (ppgSignal == null || ppgSignal.getRawData() == null) {
            return createErrorMetrics("Invalid PPG signal data");
        }
        
        List<Double> rawData = ppgSignal.getRawData();
        double samplingRate = ppgSignal.getSamplingRate();
        
        // Preprocess the signal
        List<Double> processedSignal = signalProcessor.preprocessSignal(rawData, samplingRate);
        
        if (processedSignal.isEmpty()) {
            return createErrorMetrics("Signal processing failed");
        }
        
        // Detect peaks for heart rate calculation
        List<Integer> peaks = signalProcessor.detectPeaks(processedSignal);
        
        // Calculate health metrics
        HealthMetrics metrics = new HealthMetrics();
        metrics.setId(java.util.UUID.randomUUID().toString());
        metrics.setUserId(ppgSignal.getUserId());
        metrics.setDeviceId(ppgSignal.getDeviceId());
        metrics.setTimestamp(LocalDateTime.now());
        
        // Signal quality assessment
        String signalQuality = signalProcessor.assessSignalQuality(processedSignal);
        metrics.setSignalQuality(signalQuality);
        
        // Calculate heart rate
        if (peaks.size() >= 2) {
            double heartRate = signalProcessor.calculateHeartRate(peaks, samplingRate);
            metrics.setHeartRate(Math.round(heartRate * 10.0) / 10.0);
        } else {
            metrics.setHeartRate(0.0);
        }
        
        // Calculate HRV
        if (peaks.size() >= 3) {
            double hrv = signalProcessor.calculateHRV(peaks, samplingRate);
            metrics.setHrv(Math.round(hrv * 10.0) / 10.0);
            
            // Estimate stress level based on HRV
            double stressLevel = estimateStressLevel(hrv);
            metrics.setStressLevel(stressLevel);
            
            // Detect arrhythmia
            boolean hasArrhythmia = signalProcessor.detectArrhythmia(peaks, samplingRate);
            metrics.setIrregularHeartbeat(hasArrhythmia);
        } else {
            metrics.setHrv(0.0);
            metrics.setStressLevel(0.0);
            metrics.setIrregularHeartbeat(false);
        }
        
        // SpO2 estimation (placeholder - would need dual-wavelength data)
        double spo2 = estimateSpO2(processedSignal, signalQuality);
        metrics.setSpo2(spo2);
        
        return metrics;
    }
    
    private HealthMetrics createErrorMetrics(String errorMessage) {
        HealthMetrics metrics = new HealthMetrics();
        metrics.setId(java.util.UUID.randomUUID().toString());
        metrics.setHeartRate(0.0);
        metrics.setSpo2(0.0);
        metrics.setHrv(0.0);
        metrics.setStressLevel(0.0);
        metrics.setIrregularHeartbeat(false);
        metrics.setSignalQuality("poor");
        metrics.setTimestamp(LocalDateTime.now());
        return metrics;
    }
    
    private double estimateStressLevel(double hrv) {
        // HRV-based stress estimation
        // Lower HRV generally indicates higher stress
        if (hrv < 20) {
            return 80.0; // High stress
        } else if (hrv < 50) {
            return 60.0; // Moderate stress
        } else if (hrv < 100) {
            return 40.0; // Low stress
        } else {
            return 20.0; // Very low stress
        }
    }
    
    private double estimateSpO2(List<Double> signal, String signalQuality) {
        // This is a simplified SpO2 estimation
        // In reality, this would require dual-wavelength PPG data (red and infrared)
        
        if ("poor".equals(signalQuality)) {
            return 0.0;
        }
        
        // Placeholder calculation based on signal quality
        double baseSpO2 = 98.0;
        
        switch (signalQuality) {
            case "excellent":
                return baseSpO2;
            case "good":
                return baseSpO2 - 1.0;
            case "fair":
                return baseSpO2 - 2.0;
            default:
                return 0.0;
        }
    }
    
    public String generateHealthSummary(HealthMetrics metrics) {
        StringBuilder summary = new StringBuilder();
        
        summary.append("Health Analysis Summary:\n");
        summary.append("======================\n");
        
        if (metrics.getHeartRate() > 0) {
            summary.append(String.format("Heart Rate: %.1f BPM\n", metrics.getHeartRate()));
            
            if (metrics.getHeartRate() < 60) {
                summary.append("Status: Bradycardia (Low heart rate)\n");
            } else if (metrics.getHeartRate() > 100) {
                summary.append("Status: Tachycardia (High heart rate)\n");
            } else {
                summary.append("Status: Normal heart rate\n");
            }
        }
        
        if (metrics.getSpo2() > 0) {
            summary.append(String.format("SpO2: %.1f%%\n", metrics.getSpo2()));
            
            if (metrics.getSpo2() < 90) {
                summary.append("Status: Low oxygen saturation - seek medical attention\n");
            } else if (metrics.getSpo2() < 95) {
                summary.append("Status: Below optimal oxygen saturation\n");
            } else {
                summary.append("Status: Normal oxygen saturation\n");
            }
        }
        
        if (metrics.getHrv() > 0) {
            summary.append(String.format("HRV: %.1f ms\n", metrics.getHrv()));
            summary.append(String.format("Stress Level: %.1f/100\n", metrics.getStressLevel()));
        }
        
        if (metrics.getIrregularHeartbeat()) {
            summary.append("⚠️ Irregular heartbeat detected - consult a doctor\n");
        }
        
        summary.append(String.format("Signal Quality: %s\n", metrics.getSignalQuality()));
        
        return summary.toString();
    }
}
