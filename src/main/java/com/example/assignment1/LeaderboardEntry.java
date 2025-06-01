package com.example.assignment1;

/**
 * Entry into the Leaderboard
 */
public class LeaderboardEntry {
    private final String user;
    private final String rap;
    private final int score;

    /**
     * Creates new Leaderboard entry
     */
    public LeaderboardEntry(String user, String rap, int score) {
        this.user = user;
        this.rap = rap;
        this.score = score;
    }

    /**
     * Gets user as string
     * @return user
     */
    public String getUser() {
        return user;
    }

    /**
     * Gets rap as string
     * @return rap
     */
    public String getRap() {
        return rap;
    }

    /**
     * Gets score as int
     * @return score
     */
    public int getScore() {
        return score;
    }
}
