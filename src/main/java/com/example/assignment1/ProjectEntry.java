package com.example.assignment1;

public class ProjectEntry {
    private final int projectId;
    private final String title;

    public ProjectEntry(int projectId, String title) {
        this.projectId = projectId;
        this.title = title;
    }

    public int getProjectId() {
        return projectId;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return title; // For ListView display
    }
}
