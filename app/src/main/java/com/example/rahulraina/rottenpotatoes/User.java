package com.example.rahulraina.rottenpotatoes;

/**
 * Created by rahulraina on 2/15/16.
 */
public class User {
    private String username;
    private String password;
    private String firstName;
    private String lastName;

    public User(String username, String password, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
