package com.example.rahulraina.rottenpotatoes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by aniruddha on 4/4/16.
 */
public abstract class RottenPotatoes {

    /**
     * HashMap from username to User Object
     */
    private static Map<String, User> users = new HashMap<>();

    /**
     * Movie to rating hashmap
     */
    private static List<Movie> ratedMovies = new ArrayList<>();

    /**
     * the testing locked user
     */
    private static User lockedUser = new User("locked", "pineapples", "Rahul", "Raina", "", false, true);

    /**
     * the current user
     */
    private static User currentUser;

    /**
     * the current movie in use
     */
    private static Movie currentMovie;


    /**
     * Adds the locked user for testing purposes
     */
    public static void addLockedUser() {
        add(lockedUser);
    }

    /********** Methods for manipulating users in the application *********/

    /**
     * Adds the given user to the map of usernames to Users
     * @param user the User to be added
     */
    public static void add(User user) {
        if (users.size() == 0) {
            currentUser = user;
        }
        users.put(user.getUsername(), user);
    }

    /**
     * Builds and provides a readable list of Users for the admin view
     * @return the List of Strings as readable by the administrator (specifies Banned/Locked status)
     */
    public static List<String> getAdminUsernameList() {
        final List<String> adminList = new ArrayList<>();
        for(final User u: users.values()) {
            adminList.add(u.getFullName() + "         Banned: " + u.getBan() + " | Locked: " + u.getIsLocked());
        }
        return adminList;
    }

    /**
     * Changes the current user of the application
     * @param user the User to switch control to
     */
    public static void switchUser(User user) {
        currentUser = user;
    }

    /**
     * @return the current user of the application
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    public static User registerUser(String username, String password, String firstname, String lastname) {
        /** Temporarily remove persistence code **/

        String urlstring = "http://dev-env.p3qg72k2sb.us-west-2.elasticbeanstalk.com//register.php?username="+username + "&pass=" + password + "&firstname=" + firstname + "&lastname=" + lastname;
        String resp = sendGetRequest(urlstring);

//        final String resp = "";
        if(resp == null) {
            return null;
        } else {
            final User newUser = new User(username, password, firstname, lastname);
            add(newUser);
            return newUser;
        }
    }

    /**
     * Attempts to log in using cached data
     * @param username username to test
     * @param password password to test
     * @return a user if successfully logged in, null or blank user otherwise
     */
    public static User localLogin(String username, String password) {
        if (!users.containsKey(username)) {
            return null;
        } else {
            final User user  = users.get(username);
            if (user.getPassword().equals(password)) {
                return user;
            } else {
                return new User("", "", "", "");
            }
        }
    }

    /**
     * Attempts to log in using backend
     * @param username username to test
     * @param password password to test
     * @return a user if successfully logged in, null or blank user otherwise
     */
    public static User onlineLogin(String username, String password) {
        final String urlstring = "http://dev-env.p3qg72k2sb.us-west-2.elasticbeanstalk.com//login.php?username="+username + "&pass=" + password;
        final String resp = sendGetRequest(urlstring);
        User user = null;
        try {
            // We need to add more checks for locks and bans
            if (resp == null) {
                return null;
            } else {
                final JSONObject jObject = new JSONObject(resp);
                final JSONArray profileHolder = jObject.getJSONArray("profile");
                final boolean isBanned = false;
                final boolean isLocked = false;
                user = new User(username, password, profileHolder.getString(0), profileHolder.getString(1), profileHolder.getString(3), isBanned, isLocked);
                add(user);
                return user;
            }
        } catch (JSONException e) {
            // Removed for checkstyle
            // e.printStackTrace();
            final Logger logger = Logger.getLogger(RottenPotatoes.class.getName());
            logger.log(Level.WARNING, "JSONException Occurred", e);
        }
        return user;
    }

    public static String updateProfile(String newPassword,
                                     String newFirstName, String newLastName,
                                     String newMajor, String newInterests) {

        final boolean nameChange = !(newFirstName.equals(currentUser.getFirstName()))
                || !(newLastName.equals(currentUser.getLastName()));

        final boolean passwordChange = !(newPassword.equals(currentUser.getPassword()));

        final boolean personalDataChange = !(newMajor.equals(currentUser.getMajor()))
                || !(newInterests.equals(currentUser.getInterests()));


        if (nameChange || passwordChange || personalDataChange) {

            boolean isFacebookUser = currentUser instanceof FacebookUser;

            if ((!isFacebookUser && "".equals(newPassword))
                    || ("".equals(newFirstName))
                    || ("".equals(newLastName))) {

                return "Invalid field(s). Please check your input!"; // Invalid field code

            } else {

                /** Temporarily remove persistence code **/

                String urlstring = "http://dev-env.p3qg72k2sb.us-west-2.elasticbeanstalk.com//update.php?username="
                        + currentUser.getUsername() + "&major=" + newMajor;
                String resp = sendGetRequest(urlstring);


//                final String resp = "";

                if (resp == null) {
                    return "Something went wrong"; // Backend Error code
                }

                currentUser.updateUser(newPassword, newFirstName, newLastName, newMajor, newInterests);
                return "Your profile was updated successfully!"; // Successful update code
            }
        } else {
            return "No changes made"; // No update necessary code
        }
    }

    public static User adminUserSearch(String username) {
        return users.get(username);
    }


    /********** Methods for manipulating movies in the application *********/

    /**
     * @param movie the movie to test
     * @return true if it has been rated
     */
    public static boolean isRated(Movie movie) {
        return ratedMovies.contains(currentMovie) && currentMovie.getAverageRating() != -1.0f;
    }

    /**
     * adds a movie to the rated list
     * @param movie the movie to add
     */
    public static void addToRated(Movie movie) {
        if (!isRated(movie)) {
            ratedMovies.add(movie);
        }
    }

    /**
     * @return the current movie
     */
    public static Movie getCurrentMovie() {
        return currentMovie;
    }

    /**
     *
     * @return true if any movies have been rated
     */
    public static boolean moviesHaveBeenRated() {
        return ratedMovies.size() != 0;
    }

    /**
     *
     * @return the list of movies that have been rated
     */
    public static List<Movie> getRatedMovies() {
        return ratedMovies;
    }

    public static List<Movie> sortMovies(List<Movie> toSort, boolean filterBoxChecked, String filterMajor) {

        Comparator<Movie> comparator = null;
        List<Movie> toReturn = toSort;

        if (filterBoxChecked && !"".equals(filterMajor)) {
            final List<Movie> other = new ArrayList<Movie>();
            for(final Movie m: toSort) {
                if (m.ratedByMajor(filterMajor)) {
                    other.add(m);
                }
            }
            comparator = new RatingMajorComparator(filterMajor);
            toReturn = other;
        } else if (filterBoxChecked) {
            comparator = new RatingComparator();
        } else if (!"".equals(filterMajor)) {
            final  List<Movie> other = new ArrayList<Movie>();
            for(final Movie m: toSort) {
                if (m.ratedByMajor(filterMajor)) {
                    other.add(m);
                }
            }
            comparator = null;
            toReturn = other;
        } else {
            return null;
        }

        if (comparator != null) {
            Collections.sort(toReturn, comparator);
        }

        return toReturn;
    }

    /********** Methods for external requests and data **********/

    public static Movie searchOMDB(String movieTitle) {
        final String urlString = "https://www.omdbapi.com/?t=" + movieTitle + "&y=&plot=short&r=json";
        final String resp = sendGetRequest(urlString);
        try{
            final JSONObject jObject = new JSONObject(resp);
            final String error = jObject.getString("Response");
            if("False".equals(error)) {
                return null;
            } else {

                final String year = jObject.getString("Year");
                final String title = jObject.getString("Title");
                final String rated = jObject.getString("Rated");
                final String released = jObject.getString("Released");
                final String runtime = jObject.getString("Runtime");
                currentMovie = new Movie(title, year, rated, released, runtime);
                if (ratedMovies.contains(currentMovie)) {
                    currentMovie = ratedMovies.get(ratedMovies.indexOf(currentMovie));
                }
                return currentMovie;
            }
        } catch (JSONException e) {
            //throw new RuntimeException(e);
            System.out.print(e.getMessage());
        }
        return null;
    }

    /**
     * connects to the api
     * @param urlString string representation of the api url
     * @return returns a string of what the url returns
     * @throws IOException
     */
    private static String sendGetRequest(String urlString) {
        try {
            final URL obj = new URL(urlString);
            final HttpURLConnection httpConnection = (HttpURLConnection) obj.openConnection();
            httpConnection.setRequestMethod("GET");
            final int responseCode = httpConnection.getResponseCode();
            if (responseCode == 200) {

                final BufferedReader responseReader = new BufferedReader(new InputStreamReader(
                        httpConnection.getInputStream()));

                String responseLine;
                final StringBuffer response = new StringBuffer();

                while ((responseLine = responseReader.readLine()) != null) {
                    response.append(responseLine);
                    response.append("/n");
                }
                responseReader.close();

                // print result
                return response.toString();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /********** Comparator classes for Movies **********/

    private static class RatingComparator implements Comparator<Movie> {
        @Override
        public int compare(Movie m1, Movie m2) {
            if(m2.getAverageRating() < m1.getAverageRating()) {
                return -1;
            } else if ( m2.getAverageRating() > m1.getAverageRating()) {
                return 1;
            }
            return 0;
        }
    }

    private static class RatingMajorComparator implements Comparator<Movie> {
        private String major;
        public RatingMajorComparator(String setMajor) {
            this.major = major;
        }

        @Override
        public int compare(Movie m1, Movie m2) {
            if(m2.getRatingByMajor(major) < m1.getRatingByMajor(major)) {
                return -1;
            } else if ( m2.getRatingByMajor(major) > m1.getRatingByMajor(major)) {
                return 1;
            }
            return 0;
        }
    }


}
