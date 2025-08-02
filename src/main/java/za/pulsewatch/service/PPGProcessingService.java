package za.pulsewatch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.pulsewatch.dto.PPGAnalysisRequest;
import za.pulsewatch.dto.PPGAnalysisResponse;
import za.pulsewatch.model.HealthMetrics;
import za.pulsewatch.model.PPGSignal;
import za.pulsewatch.dto.GamificationResponse;
import za.pulsewatch.model.Achievement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Service
public class PPGProcessingService {

    private final HealthAnalyzer healthAnalyzer;
    private final MessagePublisher messagePublisher;
    private final LowResourceSignalProcessor lowResourceSignalProcessor;
    private final GamificationService gamificationService;

    @Autowired
    public PPGProcessingService(HealthAnalyzer healthAnalyzer, 
                              MessagePublisher messagePublisher,
                              LowResourceSignalProcessor lowResourceSignalProcessor,
                              GamificationService gamificationService) {
        this.healthAnalyzer = healthAnalyzer;
        this.messagePublisher = messagePublisher;
        this.lowResourceSignalProcessor = lowResourceSignalProcessor;
        this.gamificationService = gamificationService;
    }

    public PPGAnalysisResponse processPPGSignal(PPGAnalysisRequest request) {
        long startTime = System.currentTimeMillis();
        
        PPGAnalysisResponse response = new PPGAnalysisResponse(
            request.getSessionId(),
            request.getUserId(),
            request.getDeviceId()
        );

        try {
            // Create PPGSignal from request
            PPGSignal ppgSignal = new PPGSignal();
            ppgSignal.setId(request.getSessionId());
            ppgSignal.setRawData(request.getRawData());
            ppgSignal.setSamplingRate(request.getSamplingRate());
            ppgSignal.setUserId(request.getUserId());
            ppgSignal.setDeviceId(request.getDeviceId());
            ppgSignal.setTimestamp(LocalDateTime.now());

            // Check if low-resource mode should be used
            boolean useLowResource = shouldUseLowResourceMode(request);
            
            // Analyze the signal
            HealthMetrics metrics;
            if (useLowResource) {
                metrics = processLowResource(request);
            } else {
                metrics = healthAnalyzer.analyzePPGSignal(ppgSignal);
            }
            
            // Apply gamification
            GamificationResponse gamification = applyGamification(request.getUserId(), metrics);
            
            response.setMetrics(metrics);
            response.setStatus("SUCCESS");
            response.setMessage("PPG signal analysis completed successfully");
            response.setGamificationResponse(gamification);
            
            // Publish result to message broker
            messagePublisher.publishAnalysisResult(response);
            
        } catch (Exception e) {
            response.setStatus("ERROR");
            response.setMessage("Error processing PPG signal: " + e.getMessage());
            response.setMetrics(createErrorMetrics(request.getUserId()));
            
            // Publish error to message broker
            messagePublisher.publishAnalysisResult(response);
        }

        long endTime = System.currentTimeMillis();
        response.setProcessingTimeMs(endTime - startTime);

        return response;
    }

    public PPGAnalysisResponse processRealtimePPG(PPGAnalysisRequest request) {
        PPGAnalysisResponse response = processPPGSignal(request);
        
        // Publish realtime update
        messagePublisher.publishRealtimeUpdate(request.getUserId(), response);
        
        return response;
    }

    private boolean shouldUseLowResourceMode(PPGAnalysisRequest request) {
        // Logic to determine if low-resource mode should be used
        return request.getRawData().size() < 1000 || request.getSamplingRate() < 30;
    }

    private HealthMetrics processLowResource(PPGAnalysisRequest request) {
        double[] processed = lowResourceSignalProcessor.preprocessLowResource(request.getRawData());
        int[] peaks = lowResourceSignalProcessor.detectPeaksLowResource(processed);
        
        HealthMetrics metrics = new HealthMetrics();
        metrics.setUserId(request.getUserId());
        metrics.setDeviceId(request.getDeviceId());
        
        double samplingRate = request.getSamplingRate();
        
        // Calculate metrics using low-resource algorithms
        metrics.setHeartRate(lowResourceSignalProcessor.calculateHeartRateLowResource(peaks, samplingRate));
        metrics.setSpo2(lowResourceSignalProcessor.estimateSpO2LowResource(processed));
        metrics.setHrv(lowResourceSignalProcessor.calculateHRVLowResource(peaks, samplingRate));
        metrics.setStressLevel(lowResourceSignalProcessor.calculateStressLevelLowResource(
            metrics.getHrv()));
        metrics.setIrregularHeartbeat(lowResourceSignalProcessor.detectArrhythmiaLowResource(peaks, samplingRate));
        
        // Signal quality estimation
        double signalRange = getSignalRange(processed);
        metrics.setSignalQuality(signalRange < 0.1 ? "poor" : "good");
        
        return metrics;
    }

    private GamificationResponse applyGamification(String userId, HealthMetrics metrics) {
        // Update streak
        int streak = gamificationService.updateStreak(userId, java.time.LocalDate.now());
        
        // Calculate health score
        int healthScore = gamificationService.calculateHealthScore(
            metrics.getHeartRate(), 
            metrics.getSpo2(), 
            metrics.getHrv(), 
            metrics.getStressLevel()
        );
        
        // Check for new achievements
        List<Achievement> newAchievements = gamificationService.checkAchievements(
            userId, 
            metrics.getHeartRate(), 
            metrics.getSpo2(), 
            metrics.getHrv(), 
            metrics.getStressLevel(), 
            streak
        );
        
        // Get virtual coach feedback
        String feedback = gamificationService.getVirtualCoachFeedback(
            userId, 
            metrics.getHeartRate(), 
            metrics.getSpo2(), 
            metrics.getHrv(), 
            metrics.getStressLevel()
        );
        
        // Get leaderboard position
        int leaderboardPosition = gamificationService.getLeaderboardPosition(userId);
        
        return new GamificationResponse(
            gamificationService.getUserStats(userId),
            new ArrayList<>(), // achievements list
            newAchievements,
            feedback,
            leaderboardPosition,
            healthScore
        );
    }

    private double getSignalRange(double[] signal) {
        double max = java.util.Arrays.stream(signal).max().orElse(0.0);
        double min = java.util.Arrays.stream(signal).min().orElse(0.0);
        return max - min;
    }

    private HealthMetrics createErrorMetrics(String userId) {
        HealthMetrics metrics = new HealthMetrics();
        metrics.setUserId(userId);
        metrics.setHeartRate(0.0);
        metrics.setSpo2(0.0);
        metrics.setHrv(0.0);
        metrics.setStressLevel(0.0);
        metrics.setIrregularHeartbeat(false);
        metrics.setSignalQuality("poor");
        metrics.setTimestamp(LocalDateTime.now());
        return metrics;
    }
}
