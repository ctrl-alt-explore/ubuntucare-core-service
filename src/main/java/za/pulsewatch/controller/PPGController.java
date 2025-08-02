package za.pulsewatch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import za.pulsewatch.dto.PPGAnalysisRequest;
import za.pulsewatch.dto.PPGAnalysisResponse;
import za.pulsewatch.dto.GamificationResponse;
import za.pulsewatch.dto.ApiResponse;
import za.pulsewatch.model.HealthMetrics;
import za.pulsewatch.model.Achievement;
import za.pulsewatch.service.PPGProcessingService;
import za.pulsewatch.service.GamificationService;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/ppg")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class PPGController {

    private final PPGProcessingService ppgProcessingService;
    private final GamificationService gamificationService;

    @Autowired
    public PPGController(PPGProcessingService ppgProcessingService, GamificationService gamificationService) {
        this.ppgProcessingService = ppgProcessingService;
        this.gamificationService = gamificationService;
    }

    @PostMapping("/analyze")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<PPGAnalysisResponse>> analyzePPGSignal(@Valid @RequestBody PPGAnalysisRequest request) {
        PPGAnalysisResponse response = ppgProcessingService.processPPGSignal(request);
        return ResponseEntity.ok(ApiResponse.success("PPG signal processed successfully", response));
    }

    @PostMapping("/realtime")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<PPGAnalysisResponse>> analyzeRealtimePPG(@Valid @RequestBody PPGAnalysisRequest request) {
        PPGAnalysisResponse response = ppgProcessingService.processRealtimePPG(request);
        return ResponseEntity.ok(ApiResponse.success("Real-time PPG signal processed successfully", response));
    }

    @PostMapping("/batch")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<PPGAnalysisResponse>>> batchAnalyzePPG(@Valid @RequestBody List<PPGAnalysisRequest> requests) {
        List<PPGAnalysisResponse> responses = new ArrayList<>();
        
        for (PPGAnalysisRequest request : requests) {
            PPGAnalysisResponse response = ppgProcessingService.processPPGSignal(request);
            responses.add(response);
        }
        
        return ResponseEntity.ok(ApiResponse.success("Batch PPG signals processed successfully", responses));
    }

    @GetMapping("/health/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserHealthSummary(@PathVariable String userId) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("userId", userId);
        summary.put("lastAnalysis", LocalDateTime.now());
        summary.put("status", "active");
        return ResponseEntity.ok(ApiResponse.success("Health summary retrieved successfully", summary));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, String>>> getSystemStatus() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "healthy");
        status.put("timestamp", LocalDateTime.now().toString());
        status.put("version", "1.0.0");
        status.put("uptime", "running");
        return ResponseEntity.ok(ApiResponse.success("System status retrieved successfully", status));
    }

    @GetMapping("/gamification/{userId}")
    public ResponseEntity<ApiResponse<GamificationResponse>> getGamificationData(@PathVariable String userId) {
        // dummy health metrics for gamification calculation
        HealthMetrics metrics = new HealthMetrics();
        metrics.setHeartRate(75.0);
        metrics.setSpo2(98.0);
        metrics.setHrv(0.08);
        metrics.setStressLevel(1.5);
        
        // Create comprehensive gamification response
        int healthScore = gamificationService.calculateHealthScore(
            75.0, 98.0, 0.08, 1.5
        );
        
        List<Achievement> newAchievements = gamificationService.checkAchievements(
            userId, 75.0, 98.0, 0.08, 1.5, 0
        );
        
        String feedback = gamificationService.getVirtualCoachFeedback(
            userId, 75.0, 98.0, 0.08, 1.5
        );
        
        int leaderboardPosition = gamificationService.getLeaderboardPosition(userId);
        
        GamificationResponse gamification = new GamificationResponse(
            gamificationService.getUserStats(userId),
            new ArrayList<>(), // achievements list
            newAchievements,
            feedback,
            leaderboardPosition,
            healthScore
        );
        
        return ResponseEntity.ok(ApiResponse.success("Gamification data retrieved successfully", gamification));
    }

    @PostMapping("/demo")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<PPGAnalysisResponse>> demoAnalysis() {
        PPGAnalysisRequest demoRequest = new PPGAnalysisRequest();
        demoRequest.setUserId("demo-user");
        demoRequest.setDeviceId("demo-device");
        demoRequest.setSamplingRate(50.0);
        demoRequest.setRawData(generateDemoPPGSignal(50.0, 72));
        
        PPGAnalysisResponse response = ppgProcessingService.processPPGSignal(demoRequest);
        return ResponseEntity.ok(ApiResponse.success("Demo PPG analysis completed successfully", response));
    }

    private List<Double> generateDemoPPGSignal(double samplingRate, double heartRateBPM) {
        List<Double> signal = new ArrayList<>();
        int samples = (int) (samplingRate * 10); // 10 seconds of data
        double heartRateHz = heartRateBPM / 60.0;
        
        for (int i = 0; i < samples; i++) {
            double t = i / samplingRate;
            double baseline = 1.0;
            double cardiac = 0.3 * Math.sin(2 * Math.PI * heartRateHz * t);
            double respiration = 0.05 * Math.sin(2 * Math.PI * 0.2 * t);
            double noise = 0.02 * (Math.random() - 0.5);
            double value = baseline + cardiac + respiration + noise;
            signal.add(Math.max(0, value));
        }
        
        return signal;
    }
}
