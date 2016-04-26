package com.example.rahulraina.rottenpotatoes;

/**
 * Created by rahulraina on 3/16/16.
 */
public final class Admin {

    /**
     * The master admin username
     */
    private static final String USERNAME = "Admin";

    /**
     * The master admin password
     */
    private static final String PASSWORD = "Admin";

    /**
     * Username getter
     * @return the username of the Admin
     */
    public static String getUsername() {
        return USERNAME;
    }

    /**
     * Password getter
     * @return the password of the Admin
     */
    public static String getPassword() {
        return PASSWORD;
    }

    /**
     * Checks if a user is the Admin
     * @param user the user to check
     * @return true if the user is the admin, false otherwise
     */
    public static boolean isAdmin(User user) {
        return user.getUsername().equals(Admin.getUsername())
                && user.getPassword().equals(Admin.getPassword());
    }



}
