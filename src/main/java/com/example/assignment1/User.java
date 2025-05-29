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

    /**
     * Get id
     * @return id
     */
    public int getId() {return id;}

    /**
     * set id
     * @param id id
     */
    public void setId(int id) {this.id = id;}

    /**
     * get username
     * @return username
     */
    public String getUsername() {return username;}

    /**
     * set username
     * @param username username
     */
    public void setUsername(String username) {this.username = username;}

    /**
     * get password
     * @return password
     */
    public String getPassword() {return password;}

    /**
     * set password
     * @param password password
     */
    public void setPassword(String password) {this.password = password;}

    /**
     * get name
     * @return name
     */
    public String getName() {return name;}

    /**
     * set name
     * @param name name
     */
    public void setName(String name) {this.name = name;}

    /**
     * get email
     * @return email
     */
    public String getEmail() {return email;}

    /**
     * set email
     * @param email email
     */
    public void setEmail(String email) {this.email = email;}
}
