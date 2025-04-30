package com.example.assignment1;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class LeaderboardEntry {
    private final SimpleStringProperty user;
    private final SimpleStringProperty rap;
    private final SimpleIntegerProperty score;

    public LeaderboardEntry(String user, String rap, int score) {
        this.user = new SimpleStringProperty(user);
        this.rap = new SimpleStringProperty(rap);
        this.score = new SimpleIntegerProperty(score);
    }

    public String getUser() { return user.get(); }
    public String getRap() { return rap.get(); }
    public int getScore() { return score.get(); }
}
