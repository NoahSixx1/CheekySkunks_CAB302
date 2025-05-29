package com.example.assignment1;

/**
 * Class denoting a saved user
 */
public class User {
    private int id;
    private String username;
    private String password;
    private String name;
    private String email;

    /**
     * Creates new user object
     * @param id current id of user
     * @param username current username of user
     * @param password current password of user
     * @param name current name of user
     * @param email current email of user
     */
    public User(int id, String username, String password, String name, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public String getUsername() {return username;}

    public void setUsername(String username) {this.username = username;}

    public String getPassword() {return password;}

    public void setPassword(String password) {this.password = password;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}
}
