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

    /**
     * get userid
     * @return userid
     */
    public int getUserid() { return userid; }

    /**
     * set userid
     * @param userid userid
     */
    public void setUserid(int userid) { this.userid = userid; }

    /**
     * get projectid
     * @return projectid
     */
    public int getProjectid() { return projectid; }

    /**
     * set projectid
     * @param projectid projectid
     */
    public void setProjectid(int projectid) { this.projectid = projectid; }

    /**
     * get transcript
     * @return transcript
     */
    public String getTranscript() { return transcript; }

    /**
     * set transcript
     * @param transcript transcript
     */
    public void setTranscript(String transcript) { this.transcript = transcript; }

}
