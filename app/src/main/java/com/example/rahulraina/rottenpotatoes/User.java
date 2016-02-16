package com.example.rahulraina.rottenpotatoes;

/**
 * Created by rahulraina on 2/15/16.
 */
public class User {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String major = "";
    private String interests = "";

    public User(String username, String password, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }

    public String getMajor() {
        return major;
    }

    public String getInterests() {
        return interests;
    }

    public void updateUser(String username, String password,
                           String firstName, String lastName,
                           String major, String interests) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.major = major;
        this.interests = interests;
    }

}
