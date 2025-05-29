package com.example.assignment1;

/**
 * Class denoting a saved project
 */
public class Project {
    private int userid;
    private int projectid;
    private String transcript;

    /**
     * Creates a new project object
     * @param userid curent ID of user
     * @param projectid current ID of project
     * @param transcript current transcript
     */
    public Project(int userid, int projectid, String transcript) {
        this.userid = userid;
        this.projectid = projectid;
        this.transcript = transcript;
    }

    public int getUserid() { return userid; }

    public void setUserid(int userid) { this.userid = userid; }

    public int getProjectid() { return projectid; }

    public void setProjectid(int projectid) { this.projectid = projectid; }

    public String getTranscript() { return transcript; }

    public void setTranscript(String transcript) { this.transcript = transcript; }

}
