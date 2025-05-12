package com.example.assignment1;

public class Project {
    private int userid;
    private int projectid;
    private String projectname;
    private String transcript;

    public Project(int userid, int projectid, String projectname) {
        this.userid = userid;
        this.projectid = projectid;
        if (projectname == null) {
            this.projectname = "New Project";
        }
        else {
            this.projectname = projectname;
        }
    }

    public int getUserid() { return userid; }

    public void setUserid(int userid) { this.userid = userid; }

    public int getProjectid() { return projectid; }

    public void setProjectid(int projectid) { this.projectid = projectid; }

    public String getProjectname() { return projectname; }

    public void setProjectname(String projectname) { this.projectname = projectname; }

    public String getTranscript() { return transcript; }

    public void setTranscript(String transcript) { this.transcript = transcript; }

}
