package com.example.rahulraina.rottenpotatoes;

/**
 * Created by rahulraina on 4/26/16.
 */
public class FacebookUser extends User {
    private String username;
    private String first_name;
    private String last_name;


    public FacebookUser(String setUsername, String setFirstName, String setLastName) {
        super(setUsername, setFirstName,setLastName);
    }


}
