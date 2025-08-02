package za.pulsewatch.service;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SignalProcessor {
    
    private static final double LOW_FREQ = 0.5;  // Hz
    private static final double HIGH_FREQ = 5.0; // Hz
    private static final int WINDOW_SIZE = 5;
    
    public List<Double> preprocessSignal(List<Double> rawData, double samplingRate) {
        if (rawData == null || rawData.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Step 1: Remove DC component
        List<Double> detrended = removeDCComponent(rawData);
        
        // Step 2: Apply bandpass filter (0.5-5 Hz for PPG)
        List<Double> filtered = applyBandpassFilter(detrended, samplingRate);
        
        // Step 3: Smooth signal using Savitzky-Golay filter
        List<Double> smoothed = applySavitzkyGolayFilter(filtered, WINDOW_SIZE);
        
        // Step 4: Normalize signal
        return normalizeSignal(smoothed);
    }
    
    private List<Double> removeDCComponent(List<Double> signal) {
        double mean = signal.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        return signal.stream()
                .map(val -> val - mean)
                .collect(Collectors.toList());
    }
    
    private List<Double> applyBandpassFilter(List<Double> signal, double samplingRate) {
        // Butterworth bandpass filter implementation
        double lowCutoff = LOW_FREQ / (samplingRate / 2);
        double highCutoff = HIGH_FREQ / (samplingRate / 2);
        
        // Simple IIR filter implementation
        return applyButterworthFilter(signal, lowCutoff, highCutoff);
    }
    
    private List<Double> applyButterworthFilter(List<Double> signal, double lowCutoff, double highCutoff) {
        List<Double> filtered = new ArrayList<>(signal);
        
        // Simple implementation - in production, use proper filter design
        double alpha = 0.1; // smoothing factor
        
        for (int i = 1; i < filtered.size(); i++) {
            filtered.set(i, alpha * filtered.get(i) + (1 - alpha) * filtered.get(i - 1));
        }
        
        return filtered;
    }
    
    private List<Double> applySavitzkyGolayFilter(List<Double> signal, int windowSize) {
        if (signal.size() < windowSize) {
            return signal;
        }
        
        List<Double> smoothed = new ArrayList<>(signal.size());
        int halfWindow = windowSize / 2;
        
        for (int i = 0; i < signal.size(); i++) {
            int start = Math.max(0, i - halfWindow);
            int end = Math.min(signal.size(), i + halfWindow + 1);
            
            DescriptiveStatistics stats = new DescriptiveStatistics();
            for (int j = start; j < end; j++) {
                stats.addValue(signal.get(j));
            }
            
            smoothed.add(stats.getMean());
        }
        
        return smoothed;
    }
    
    private List<Double> normalizeSignal(List<Double> signal) {
        if (signal.isEmpty()) return signal;
        
        double min = signal.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
        double max = signal.stream().mapToDouble(Double::doubleValue).max().orElse(1.0);
        double range = max - min;
        
        if (range == 0) {
            return signal.stream().map(val -> 0.0).collect(Collectors.toList());
        }
        
        return signal.stream()
                .map(val -> (val - min) / range)
                .collect(Collectors.toList());
    }
    
    public List<Integer> detectPeaks(List<Double> signal) {
        List<Integer> peaks = new ArrayList<>();
        
        if (signal.size() < 3) {
            return peaks;
        }
        
        for (int i = 1; i < signal.size() - 1; i++) {
            if (signal.get(i) > signal.get(i - 1) && 
                signal.get(i) > signal.get(i + 1) &&
                signal.get(i) > 0.3) { // amplitude threshold
                peaks.add(i);
            }
        }
        
        return peaks;
    }
    
    public double calculateHeartRate(List<Integer> peaks, double samplingRate) {
        if (peaks.size() < 2) {
            return 0.0;
        }
        
        List<Double> intervals = new ArrayList<>();
        for (int i = 1; i < peaks.size(); i++) {
            double interval = (peaks.get(i) - peaks.get(i - 1)) / samplingRate;
            intervals.add(interval);
        }
        
        double avgInterval = intervals.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        return 60.0 / avgInterval; // Convert to BPM
    }
    
    public double calculateHRV(List<Integer> peaks, double samplingRate) {
        if (peaks.size() < 3) {
            return 0.0;
        }
        
        List<Double> intervals = new ArrayList<>();
        for (int i = 1; i < peaks.size(); i++) {
            double interval = (peaks.get(i) - peaks.get(i - 1)) / samplingRate * 1000; // Convert to ms
            intervals.add(interval);
        }
        
        DescriptiveStatistics stats = new DescriptiveStatistics();
        intervals.forEach(stats::addValue);
        
        return stats.getStandardDeviation(); // SDNN in milliseconds
    }
    
    public boolean detectArrhythmia(List<Integer> peaks, double samplingRate) {
        if (peaks.size() < 3) {
            return false;
        }
        
        List<Double> intervals = new ArrayList<>();
        for (int i = 1; i < peaks.size(); i++) {
            double interval = (peaks.get(i) - peaks.get(i - 1)) / samplingRate;
            intervals.add(interval);
        }
        
        DescriptiveStatistics stats = new DescriptiveStatistics();
        intervals.forEach(stats::addValue);
        
        double mean = stats.getMean();
        double stdDev = stats.getStandardDeviation();
        
        // Check for irregular intervals (coefficient of variation > 15%)
        double cv = (stdDev / mean) * 100;
        return cv > 15.0;
    }
    
    public String assessSignalQuality(List<Double> signal) {
        if (signal == null || signal.isEmpty()) {
            return "poor";
        }
        
        DescriptiveStatistics stats = new DescriptiveStatistics();
        signal.forEach(stats::addValue);
        
        double snr = calculateSNR(signal);
        double zeroCrossingRate = calculateZeroCrossingRate(signal);
        
        if (snr > 20 && zeroCrossingRate < 0.3) {
            return "excellent";
        } else if (snr > 10 && zeroCrossingRate < 0.5) {
            return "good";
        } else if (snr > 5) {
            return "fair";
        } else {
            return "poor";
        }
    }
    
    private double calculateSNR(List<Double> signal) {
        // Simple SNR estimation
        DescriptiveStatistics stats = new DescriptiveStatistics();
        signal.forEach(stats::addValue);
        
        double signalPower = Math.pow(stats.getMean(), 2);
        double noisePower = Math.pow(stats.getStandardDeviation(), 2);
        
        return 10 * Math.log10(signalPower / noisePower);
    }
    
    private double calculateZeroCrossingRate(List<Double> signal) {
        int zeroCrossings = 0;
        for (int i = 1; i < signal.size(); i++) {
            if ((signal.get(i - 1) > 0 && signal.get(i) <= 0) || 
                (signal.get(i - 1) <= 0 && signal.get(i) > 0)) {
                zeroCrossings++;
            }
        }
        return (double) zeroCrossings / signal.size();
    }
}
