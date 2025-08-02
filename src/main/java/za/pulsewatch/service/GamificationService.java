package za.pulsewatch.service;

import org.springframework.stereotype.Service;
import za.pulsewatch.model.Achievement;
import za.pulsewatch.model.UserStats;

import java.time.LocalDate;
import java.util.*;

@Service
public class GamificationService {

    private final Map<String, UserStats> userStats = new HashMap<>();
    private final Map<String, List<Achievement>> userAchievements = new HashMap<>();

    /**
     * Track daily monitoring streak
     */
    public int updateStreak(String userId, LocalDate date) {
        UserStats stats = getUserStats(userId);
        
        if (date.equals(stats.getLastCheckDate())) {
            return stats.getCurrentStreak();
        }
        
        if (date.equals(stats.getLastCheckDate().plusDays(1))) {
            stats.setCurrentStreak(stats.getCurrentStreak() + 1);
            stats.setLongestStreak(Math.max(stats.getLongestStreak(), stats.getCurrentStreak()));
        } else {
            stats.setCurrentStreak(1);
        }
        
        stats.setLastCheckDate(date);
        stats.setTotalSessions(stats.getTotalSessions() + 1);
        

        
        return stats.getCurrentStreak();
    }

    /**
     * Calculate health score based on metrics
     */
    public int calculateHealthScore(double heartRate, double spo2, double hrv, double stressLevel) {
        int score = 0;
        
        // Heart rate score (60-100 BPM is optimal)
        if (heartRate >= 60 && heartRate <= 100) {
            score += 25;
        } else if (heartRate >= 50 && heartRate <= 120) {
            score += 15;
        }
        
        // SpO2 score (95%+ is optimal)
        if (spo2 >= 95) {
            score += 25;
        } else if (spo2 >= 90) {
            score += 15;
        }
        
        // HRV score (higher is better)
        if (hrv > 0.05) {
            score += 25;
        } else if (hrv > 0.02) {
            score += 15;
        }
        
        // Stress score (lower is better)
        if (stressLevel <= 1.5) {
            score += 25;
        } else if (stressLevel <= 2.5) {
            score += 15;
        }
        
        return Math.min(100, Math.max(0, score));
    }

    /**
     * Award achievements based on milestones
     */
    public List<Achievement> checkAchievements(String userId, double heartRate, double spo2, 
                                               double hrv, double stressLevel, int streak) {
        List<Achievement> newAchievements = new ArrayList<>();
        UserStats stats = getUserStats(userId);
        
        // Health score achievement
        int healthScore = calculateHealthScore(heartRate, spo2, hrv, stressLevel);
        if (healthScore >= 90 && !hasAchievement(userId, "HEALTH_EXCELLENT")) {
            newAchievements.add(createAchievement("HEALTH_EXCELLENT", "Excellent Health", 
                "Achieved 90+ health score"));
        }
        
        // Streak achievements
        if (streak >= 7 && !hasAchievement(userId, "WEEKLY_STREAK")) {
            newAchievements.add(createAchievement("WEEKLY_STREAK", "Weekly Warrior", 
                "7-day monitoring streak"));
        }
        
        if (streak >= 30 && !hasAchievement(userId, "MONTHLY_STREAK")) {
            newAchievements.add(createAchievement("MONTHLY_STREAK", "Monthly Master", 
                "30-day monitoring streak"));
        }
        
        if (streak >= 100 && !hasAchievement(userId, "CENTURY_STREAK")) {
            newAchievements.add(createAchievement("CENTURY_STREAK", "Century Champion", 
                "100-day monitoring streak"));
        }
        
        // Total sessions achievements
        if (stats.getTotalSessions() >= 50 && !hasAchievement(userId, "REGULAR_MONITOR")) {
            newAchievements.add(createAchievement("REGULAR_MONITOR", "Regular Monitor", 
                "Completed 50 monitoring sessions"));
        }
        
        if (stats.getTotalSessions() >= 200 && !hasAchievement(userId, "HEALTH_CHAMPION")) {
            newAchievements.add(createAchievement("HEALTH_CHAMPION", "Health Champion", 
                "Completed 200 monitoring sessions"));
        }
        
        // Add new achievements
        for (Achievement achievement : newAchievements) {
            addAchievement(userId, achievement);
        }
        
        return newAchievements;
    }

    /**
     * Get virtual coach feedback
     */
    public String getVirtualCoachFeedback(String userId, double heartRate, double spo2, 
                                         double hrv, double stressLevel) {
        UserStats stats = getUserStats(userId);
        int healthScore = calculateHealthScore(heartRate, spo2, hrv, stressLevel);
        
        StringBuilder feedback = new StringBuilder();
        
        // General health assessment
        if (healthScore >= 80) {
            feedback.append("Excellent health metrics! Keep up the great work! ");
        } else if (healthScore >= 60) {
            feedback.append("Good health metrics. There's room for improvement. ");
        } else {
            feedback.append("Your health metrics need attention. Let's work on improving them. ");
        }
        
        // Specific recommendations
        if (stressLevel > 2.5) {
            feedback.append("Consider stress-reduction techniques like deep breathing or meditation. ");
        }
        
        if (hrv < 0.02) {
            feedback.append("Your heart rate variability is low - ensure adequate sleep and recovery. ");
        }
        
        if (heartRate > 100 || heartRate < 50) {
            feedback.append("Your heart rate is outside the normal range. Consider consulting a healthcare professional. ");
        }
        
        // Streak motivation
        if (stats.getCurrentStreak() < 3) {
            feedback.append("Let's build a daily monitoring habit! Even 3 days can make a difference. ");
        } else {
            feedback.append(String.format("Great job on your %d-day streak! Keep it going! ", 
                stats.getCurrentStreak()));
        }
        
        return feedback.toString();
    }

    /**
     * Get user leaderboard position
     */
    public int getLeaderboardPosition(String userId) {
        // This is a simplified version - in production, this would query a database
        UserStats stats = getUserStats(userId);
        
        // Calculate score based on multiple factors
        int score = stats.getLongestStreak() * 10 + 
                   stats.getTotalSessions() * 2 + 
                   (int) (stats.getAverageHealthScore() * 5);
        
        // In a real implementation, this would rank against all users
        return Math.max(1, 1000 - score / 10); // Mock ranking
    }

    /**
     * Get user statistics
     */
    public UserStats getUserStats(String userId) {
        return userStats.computeIfAbsent(userId, k -> {
            UserStats stats = new UserStats();
            stats.setUserId(userId);
            stats.setCurrentStreak(0);
            stats.setLongestStreak(0);
            stats.setTotalSessions(0);
            stats.setAverageHealthScore(0.0);
            stats.setLastCheckDate(LocalDate.now().minusDays(1));
            return stats;
        });
    }

    private boolean hasAchievement(String userId, String achievementId) {
        return userAchievements.getOrDefault(userId, new ArrayList<>())
                .stream().anyMatch(a -> a.getId().equals(achievementId));
    }

    private void addAchievement(String userId, Achievement achievement) {
        userAchievements.computeIfAbsent(userId, k -> new ArrayList<>()).add(achievement);
    }

    private Achievement createAchievement(String id, String name, String description) {
        Achievement achievement = new Achievement();
        achievement.setId(id);
        achievement.setName(name);
        achievement.setDescription(description);
        achievement.setDate(LocalDate.now());
        return achievement;
    }
}
