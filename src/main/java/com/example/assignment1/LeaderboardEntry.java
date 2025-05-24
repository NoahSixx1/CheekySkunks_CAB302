package com.example.assignment1;

public class LeaderboardEntry {
    private final String user;
    private final String rap;
    private final int score;

    public LeaderboardEntry(String user, String rap, int score) {
        this.user = user;
        this.rap = rap;
        this.score = score;
    }

    public String getUser() {
        return user;
    }

    public String getRap() {
        return rap;
    }

    public int getScore() {
        return score;
    }
}
