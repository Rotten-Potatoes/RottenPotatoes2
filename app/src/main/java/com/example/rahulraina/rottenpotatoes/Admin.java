package com.example.rahulraina.rottenpotatoes;

/**
 * Created by rahulraina on 3/16/16.
 */
public class Admin {
    private static final String username = "Admin";
    private static final String password = "Admin";

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }

    public static boolean isAdmin(User user) {
        return user.getUsername().equals(Admin.getUsername())
                && user.getPassword().equals(Admin.getPassword());
    }

}
