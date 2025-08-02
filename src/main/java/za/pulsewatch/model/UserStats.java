package za.pulsewatch.model;

import java.time.LocalDate;

public class UserStats {
    private String userId;
    private int currentStreak;
    private int longestStreak;
    private int totalSessions;
    private double averageHealthScore;
    private LocalDate lastCheckDate;
    
    // Constructors
    public UserStats() {}
    
    public UserStats(String userId) {
        this.userId = userId;
        this.currentStreak = 0;
        this.longestStreak = 0;
        this.totalSessions = 0;
        this.averageHealthScore = 0.0;
        this.lastCheckDate = LocalDate.now().minusDays(1);
    }
    
    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }
    
    public int getLongestStreak() { return longestStreak; }
    public void setLongestStreak(int longestStreak) { this.longestStreak = longestStreak; }
    
    public int getTotalSessions() { return totalSessions; }
    public void setTotalSessions(int totalSessions) { this.totalSessions = totalSessions; }
    
    public double getAverageHealthScore() { return averageHealthScore; }
    public void setAverageHealthScore(double averageHealthScore) { this.averageHealthScore = averageHealthScore; }
    
    public LocalDate getLastCheckDate() { return lastCheckDate; }
    public void setLastCheckDate(LocalDate lastCheckDate) { this.lastCheckDate = lastCheckDate; }
}
