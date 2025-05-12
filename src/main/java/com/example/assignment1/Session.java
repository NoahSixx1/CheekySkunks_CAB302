package com.example.assignment1;

public class Session {
    private static String currentUserId;
    private static String currentProjectId;
    private static Project currentProject;

    public static void setCurrentUserId(String userId) {
        currentUserId = userId;
    }

    public static String getCurrentUserId() {
        return currentUserId;
    }

    public static void setCurrentProjectId(String projectId) {
        currentProjectId = projectId;
    }

    public static String getCurrentProjectId() {
        return currentProjectId;
    }

    public static void setCurrentProject(Project project) { currentProject = project; }

    public static Project getCurrentProject() { return currentProject; }

    public static void clear() {
        currentUserId = null;
        currentProjectId = null;
        currentProject = null;
    }
}
