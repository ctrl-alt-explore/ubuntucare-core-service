package za.pulsewatch.dto;

import za.pulsewatch.model.Achievement;
import za.pulsewatch.model.UserStats;

import java.util.List;

public class GamificationResponse {
    private UserStats userStats;
    private List<Achievement> achievements;
    private List<Achievement> newAchievements;
    private String virtualCoachFeedback;
    private int leaderboardPosition;
    private int healthScore;
    
    // Constructors
    public GamificationResponse() {}
    
    public GamificationResponse(UserStats userStats, List<Achievement> achievements, 
                               List<Achievement> newAchievements, String virtualCoachFeedback,
                               int leaderboardPosition, int healthScore) {
        this.userStats = userStats;
        this.achievements = achievements;
        this.newAchievements = newAchievements;
        this.virtualCoachFeedback = virtualCoachFeedback;
        this.leaderboardPosition = leaderboardPosition;
        this.healthScore = healthScore;
    }
    
    // Getters and Setters
    public UserStats getUserStats() { return userStats; }
    public void setUserStats(UserStats userStats) { this.userStats = userStats; }
    
    public List<Achievement> getAchievements() { return achievements; }
    public void setAchievements(List<Achievement> achievements) { this.achievements = achievements; }
    
    public List<Achievement> getNewAchievements() { return newAchievements; }
    public void setNewAchievements(List<Achievement> newAchievements) { this.newAchievements = newAchievements; }
    
    public String getVirtualCoachFeedback() { return virtualCoachFeedback; }
    public void setVirtualCoachFeedback(String virtualCoachFeedback) { this.virtualCoachFeedback = virtualCoachFeedback; }
    
    public int getLeaderboardPosition() { return leaderboardPosition; }
    public void setLeaderboardPosition(int leaderboardPosition) { this.leaderboardPosition = leaderboardPosition; }
    
    public int getHealthScore() { return healthScore; }
    public void setHealthScore(int healthScore) { this.healthScore = healthScore; }
}
