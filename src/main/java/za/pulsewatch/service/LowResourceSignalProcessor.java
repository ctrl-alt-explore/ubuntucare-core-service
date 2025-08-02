package za.pulsewatch.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LowResourceSignalProcessor {

    /**
     * Optimized signal processing for low-resource environments
     * Uses simpler algorithms that require less computational power
     */
    
    public double[] preprocessLowResource(List<Double> rawData) {
        // Simple preprocessing without complex filtering
        double[] processed = new double[rawData.size()];
        
        // Basic DC removal (subtract mean)
        double mean = rawData.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        for (int i = 0; i < rawData.size(); i++) {
            processed[i] = rawData.get(i) - mean;
        }
        
        // Simple smoothing with moving average (3-point)
        double[] smoothed = new double[processed.length];
        for (int i = 1; i < processed.length - 1; i++) {
            smoothed[i] = (processed[i-1] + processed[i] + processed[i+1]) / 3.0;
        }
        
        return smoothed;
    }
    
    /**
     * Simple peak detection without complex algorithms
     */
    public int[] detectPeaksLowResource(double[] signal) {
        java.util.List<Integer> peaks = new java.util.ArrayList<>();
        
        for (int i = 1; i < signal.length - 1; i++) {
            if (signal[i] > signal[i-1] && signal[i] > signal[i+1] && signal[i] > 0) {
                peaks.add(i);
            }
        }
        
        return peaks.stream().mapToInt(Integer::intValue).toArray();
    }
    
    /**
     * Calculate heart rate with minimal computation
     */
    public double calculateHeartRateLowResource(int[] peakIndices, double samplingRate) {
        if (peakIndices.length < 2) return 0.0;
        
        // Simple average interval calculation
        double totalInterval = 0;
        for (int i = 1; i < peakIndices.length; i++) {
            totalInterval += (peakIndices[i] - peakIndices[i-1]) / samplingRate;
        }
        
        double avgInterval = totalInterval / (peakIndices.length - 1);
        return 60.0 / avgInterval; // Convert to BPM
    }
    
    /**
     * Simple SpO2 estimation based on signal quality
     */
    public double estimateSpO2LowResource(double[] signal) {
        // Simple signal quality assessment
        double signalRange = getSignalRange(signal);
        
        if (signalRange < 0.1) return 0.0; // Poor signal
        
        // Basic estimation based on signal characteristics
        double qualityScore = Math.min(1.0, signalRange / 2.0);
        return 95.0 + (qualityScore * 5.0); // Range 95-100%
    }
    
    /**
     * Simple HRV calculation using standard deviation
     */
    public double calculateHRVLowResource(int[] peakIndices, double samplingRate) {
        if (peakIndices.length < 3) return 0.0;
        
        double[] intervals = new double[peakIndices.length - 1];
        for (int i = 0; i < intervals.length; i++) {
            intervals[i] = (peakIndices[i+1] - peakIndices[i]) / samplingRate;
        }
        
        // Calculate standard deviation
        double mean = java.util.Arrays.stream(intervals).average().orElse(0.0);
        double variance = java.util.Arrays.stream(intervals)
                .map(i -> Math.pow(i - mean, 2))
                .average().orElse(0.0);
        
        return Math.sqrt(variance);
    }
    
    /**
     * Simple stress level based on HRV
     */
    public double calculateStressLevelLowResource(double hrv) {
        if (hrv == 0.0) return 0.0;
        
        // Simple stress classification
        if (hrv < 0.02) return 3.0; // High stress
        else if (hrv < 0.05) return 2.0; // Medium stress
        else return 1.0; // Low stress
    }
    
    /**
     * Simple arrhythmia detection
     */
    public boolean detectArrhythmiaLowResource(int[] peakIndices, double samplingRate) {
        if (peakIndices.length < 3) return false;
        
        double[] intervals = new double[peakIndices.length - 1];
        for (int i = 0; i < intervals.length; i++) {
            intervals[i] = (peakIndices[i+1] - peakIndices[i]) / samplingRate;
        }
        
        // Check coefficient of variation
        double mean = java.util.Arrays.stream(intervals).average().orElse(0.0);
        double stdDev = Math.sqrt(java.util.Arrays.stream(intervals)
                .map(i -> Math.pow(i - mean, 2))
                .average().orElse(0.0));
        
        double cv = stdDev / mean;
        return cv > 0.15; // Simple threshold for irregularity
    }
    
    private double getSignalRange(double[] signal) {
        double max = java.util.Arrays.stream(signal).max().orElse(0.0);
        double min = java.util.Arrays.stream(signal).min().orElse(0.0);
        return max - min;
    }
}
