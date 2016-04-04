package com.example.rahulraina.rottenpotatoes;

/**
 * Created by rahulraina on 2/15/16.
 */
public class User {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String major;
    private String interests;

    private boolean isBanned;
    private boolean isLocked;

    /**
     * Constructor for the user
     * @param username username to log in
     * @param password passowred to log in
     * @param firstName firstname of user
     * @param lastName last name of user
     */
    public User(String username, String password, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        major = "";
        interests = "";
        isBanned = false;
        isLocked = false;
    }

    public User(String username, String password, String firstName, String lastName, String major, boolean isBanned, boolean isLocked) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isBanned = isBanned;
        this.isLocked = isLocked;
        this.interests = "";
        this.major = major;
    }

    /**
     * gets the username
     * @return string ussername
     */
    public String getUsername() {
        return username;
    }

    /**
     * gets the password
     * @return tthe password
     */
    public String getPassword() {
        return password;
    }

    /**
     * gets the firstname of the user
     * @return string first name of user
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * gets the last name of the user
     * @return the last name of the user
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * gets the full name of the user
     * @return String the full name of the user
     */
    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }

    /**
     * geths the major of the user
     * @return String major of the user
     */
    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void setBan(boolean ban) {
        isBanned = ban;
    }

    public boolean getBan() {
        return isBanned;
    }

    public boolean getIsLocked() {
        return isLocked;
    }

    public void setLock(boolean lock) {
        isLocked = lock;
    }

    /**
     * Gets the interests of the user
     * @return String intersts of user
     */
    public String getInterests() {
        return interests;
    }

    /**
     * Updates the user when updated
     * @param username update the username
     * @param password upate the passord
     * @param firstName update the firstname
     * @param lastName update the last name
     * @param major update the major
     * @param interests update the interests
     */
    public void updateUser(String password,
                           String firstName, String lastName,
                           String major, String interests) {
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.major = major;
        this.interests = interests;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof User)) {
            return false;
        } else if (this == other) {
            return true;
        } else {
            User that = (User) (other);
            return this.username == username
                    && this.password == password
                    && this.firstName == firstName
                    && this.lastName == lastName
                    && this.major == major
                    && this.interests == interests;
        }
    }

}
