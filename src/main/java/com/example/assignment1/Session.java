package com.example.assignment1;

/**
 * Session for recording audio for API function
 */
public class Session {
    private static String currentUserId;
    private static String currentProjectId;

    /**
     * set userid
     * @param userId userid
     */
    public static void setCurrentUserId(String userId) {
        currentUserId = userId;
    }

    /**
     * get userid
     * @return userid
     */
    public static String getCurrentUserId() {
        return currentUserId;
    }

    /**
     * set projectid
     * @param projectId projectid
     */
    public static void setCurrentProjectId(String projectId) {
        currentProjectId = projectId;
    }

    /**
     * get projectid
     * @return projectid
     */
    public static String getCurrentProjectId() {
        return currentProjectId;
    }

    /**
     * clear values
     */
    public static void clear() {
        currentUserId = null;
        currentProjectId = null;
    }
}
