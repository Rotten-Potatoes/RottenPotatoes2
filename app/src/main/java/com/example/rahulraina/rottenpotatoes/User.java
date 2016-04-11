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
    public User(String setUsername, String setPassword, String setFirstName, String setLastName) {
        this.username = setUsername;
        this.password = setPassword;
        this.firstName = setFirstName;
        this.lastName = setLastName;
        major = "";
        interests = "";
        isBanned = false;
        isLocked = false;
    }

    public User(String setUsername, String setPassword, String setFirstName, String setLastName, String setMajor, boolean setIsBanned, boolean setIsLocked) {
        this.username = setUsername;
        this.password = setPassword;
        this.firstName = setFirstName;
        this.lastName = setLastName;
        this.isBanned = setIsBanned;
        this.isLocked = setIsLocked;
        this.interests = "";
        this.major = setMajor;
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

    public void setMajor(String setMajor) {
        this.major = setMajor;
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
     * @param password upate the passord
     * @param firstName update the firstname
     * @param lastName update the last name
     * @param major update the major
     * @param interests update the interests
     */
    public void updateUser(String setPassword,
                           String setFirstName, String setLastName,
                           String setMajor, String setInterests) {
        this.password = setPassword;
        this.firstName = setFirstName;
        this.lastName = setLastName;
        this.major = setMajor;
        this.interests = setInterests;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof User)) {
            return false;
        } else if (this == other) {
            return true;
        } else {
            final User that = (User) (other);
            return that.username == username
                    && that.password == password
                    && that.firstName == firstName
                    && that.lastName == lastName
                    && that.major == major
                    && that.interests == interests;
        }
    }

    @Override
    public int hashCode() {
        int result = 55;

        result = 37 * result + username.hashCode();
        result = 37 * result + password.hashCode();
        result = 37 * result + firstName.hashCode();
        result = 37 * result + lastName.hashCode();
        result = 37 * result + major.hashCode();
        result = 37 * result + interests.hashCode();

        return result;

    }

}
