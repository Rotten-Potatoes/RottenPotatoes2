package com.example.rahulraina.rottenpotatoes;

import android.view.View;

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

/**
 * Created by aniruddha on 4/4/16.
 */
public abstract class RottenPotatoes {

    // HashMap from username to User Object
    private static HashMap<String, User> users = new HashMap<>();

    //Hashmap for movie to rating
    private static List<Movie> ratedMovies = new ArrayList<>();

    public static User lockedUser = new User("locked", "pineapples", "Rahul", "Raina", "", false, true);

    private static User currentUser;
    private static Movie currentMovie;

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
        List<String> adminList = new ArrayList<>();
        for(User u: users.values()) {
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
        /*
        String urlstring = "http://rp-dev-env.szucsmaqnf.us-west-2.elasticbeanstalk.com/register.php?username="+username + "&pass=" + password + "&firstname=" + firstname + "&lastname=" + lastname;
        String resp = sendGetRequest(urlstring);
        */
        String resp = "";
        if(resp == null) {
            return null;
        } else {
            User newUser = new User(username, password, firstname, lastname);
            add(newUser);
            return newUser;
        }
    }

    public static User localLogin(String username, String password) {
        if (!users.containsKey(username)) {
            return null;
        } else {
            User user  = users.get(username);
            if (user.getPassword().equals(password)) {
                return user;
            } else {
                return new User("", "", "", "");
            }
        }
    }

    public static User onlineLogin(String username, String password) {
        String urlstring = "http://rp-dev-env.szucsmaqnf.us-west-2.elasticbeanstalk.com/login.php?username="+username + "&pass=" + password;
        String resp = sendGetRequest(urlstring);
        User user = null;
        try {
            // We need to add more checks for locks and bans
            if (resp == null) {
                return null;
            } else {
                JSONObject jObject = new JSONObject(resp);
                String statusmessage = jObject.getString("status_message");
                JSONArray profileHolder = jObject.getJSONArray("profile");
                boolean isBanned = false;
                boolean isLocked = false;
                user = new User(username, password, profileHolder.getString(0), profileHolder.getString(1), profileHolder.getString(3), isBanned, isLocked);
                add(user);
                return user;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static int updateProfile(String newPassword,
                                     String newFirstName, String newLastName,
                                     String newMajor, String newInterests,
                                     String finishedMajor) {


            if (!(newPassword.equals(currentUser.getPassword()))
                || !(newFirstName.equals(currentUser.getFirstName()))
                || !(newLastName.equals(currentUser.getLastName()))
                || !(newMajor.equals(currentUser.getMajor()))
                || !(newInterests.equals(currentUser.getInterests()))) {

                if ((newPassword.equals(""))
                        || (newFirstName.equals(""))
                        || (newLastName.equals(""))) {

                    return 0; // Invalid field code

                } else {

                    /** Temporarily remove persistence code **/
                    /*
                    String urlstring = "http://rp-dev-env.szucsmaqnf.us-west-2.elasticbeanstalk.com/update.php?username="
                            + currentUser.getUsername() + "&major=" + finishedMajor;
                    String resp = sendGetRequest(urlstring);
                    */
                    String resp = "";
                    if (resp == null) {
                        return 1; // Backend Error code
                    }

                    currentUser.updateUser(newPassword, newFirstName, newLastName, newMajor, newInterests);
                    return 2; // Successful update code
                }
            } else {
                return 3; // No update necessary code
            }
    }

    public static User adminUserSearch(String username) {
        return users.get(username);
    }


    /********** Methods for manipulating movies in the application *********/

    public static boolean isRated(Movie movie) {
        return ratedMovies.contains(currentMovie) && currentMovie.getAverageRating() != -1.0f;
    }

    public static void addToRated(Movie movie) {
        if (!isRated(movie)) {
            ratedMovies.add(movie);
        }
    }

    public static Movie getCurrentMovie() {
        return currentMovie;
    }

    public static boolean moviesHaveBeenRated() {
        return ratedMovies.size() != 0;
    }

    public static List<Movie> getRatedMovies() {
        return ratedMovies;
    }

    public static List<Movie> sortMovies(List<Movie> toSort, boolean filterBoxChecked, String filterMajor) {

        Comparator<Movie> comparator = null;

        if (filterBoxChecked && !filterMajor.equals("")) {
            List<Movie> other = new ArrayList<Movie>();
            for(Movie m: toSort) {
                if (m.ratedByMajor(filterMajor)) {
                    other.add(m);
                }
            }
            comparator = new RatingMajorComparator(filterMajor);
            toSort = other;
        } else if (filterBoxChecked) {
            comparator = new RatingComparator();
        } else if (!filterMajor.equals("")) {
            List<Movie> other = new ArrayList<Movie>();
            for(Movie m: toSort) {
                if (m.ratedByMajor(filterMajor)) {
                    other.add(m);
                }
            }
            comparator = null;
            toSort = other;
        } else {
            return null;
        }

        if (comparator != null) {
            Collections.sort(toSort, comparator);
        }

        return toSort;
    }

    /********** Methods for external requests and data **********/

    public static Movie searchOMDB(String movieTitle) {
        String urlString = "https://www.omdbapi.com/?t=" + movieTitle + "&y=&plot=short&r=json";
        String resp = sendGetRequest(urlString);
        try{
            JSONObject jObject = new JSONObject(resp);
            String error = jObject.getString("Response");
            if(error.equals("False")) {
                return null;
            } else {

                String year = jObject.getString("Year");
                String title = jObject.getString("Title");
                String rated = jObject.getString("Rated");
                String released = jObject.getString("Released");
                String runtime = jObject.getString("Runtime");
                currentMovie = new Movie(title, year, rated, released, runtime);
                if (ratedMovies.contains(currentMovie)) {
                    currentMovie = ratedMovies.get(ratedMovies.indexOf(currentMovie));
                }
                return currentMovie;
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
            //System.out.print(e.getMessage());
        }
    }

    /**
     * connects to the api
     * @param urlString string representation of the api url
     * @return returns a string of what the url returns
     * @throws IOException
     */
    private static String sendGetRequest(String urlString) {
        try {
            URL obj = new URL(urlString);
            HttpURLConnection httpConnection = (HttpURLConnection) obj.openConnection();
            httpConnection.setRequestMethod("GET");
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == 200) {

                BufferedReader responseReader = new BufferedReader(new InputStreamReader(
                        httpConnection.getInputStream()));

                String responseLine;
                StringBuffer response = new StringBuffer();

                while ((responseLine = responseReader.readLine()) != null) {
                    response.append(responseLine+"\n");
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
        String major;
        public RatingMajorComparator(String major) {
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
