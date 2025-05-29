package com.example.assignment1;

/**
 * Simplified project object for storing
 */
public class ProjectEntry {
    private final int projectId;
    private final String title;

    /**
     * Creates project object
     * @param projectId projectid
     * @param title title
     */
    public ProjectEntry(int projectId, String title) {
        this.projectId = projectId;
        this.title = title;
    }

    /**
     * get projectid
     */
    public int getProjectId() {
        return projectId;
    }

    /**
     * get title
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * convert to stirng
     * @return string
     */
    @Override
    public String toString() {
        return title; // For ListView display
    }
}
