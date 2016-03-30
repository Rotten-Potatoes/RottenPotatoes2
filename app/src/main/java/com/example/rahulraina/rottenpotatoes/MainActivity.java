package com.example.rahulraina.rottenpotatoes;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rahulraina.rottenpotatoes.R;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity{
    // Variables for Login Screen
    private EditText username;
    private EditText password;
    private TextView profile_button;

    private ListView lv;

    // Variables for Registration Screen
    private EditText registerUsername;
    private EditText registerPassword;
    private EditText registerFirstName;
    private EditText registerLastName;

    // HashMap from username to User Object
    HashMap<String, User> user_holder = new HashMap<>();

    //Hashmap for movie to rating
    private List<Movie> movies_rated = new ArrayList<>();

    private User currentUser;

    // Lets us know if we have to clear the Login screen password upon sign out
    private boolean cameFromLogin = false;

    // Variables for Edit Profile Screen
    private EditText editUsername;
    private EditText editPassword;
    private EditText editFirstName;
    private EditText editLastName;
    private EditText editMajor;
    private EditText editInterests;

    private Movie currentMovie;
    private TextView movieTitle;
    private TextView movieYear;
    private TextView movieRated;
    private TextView movieUserRating;

    private TextView movieTitleRate;
    private TextView movieYearRate;

    private JSONObject jObject;


    private View rateButton;
    private Float movieRating;

    private SearchView movietext;
    User selectedUser;

    private User lockedUser;
    private String lockedUsername;


    /**
     * Starts the application and the cycle. First method to execute
     * so sets the view on start to the main screen
     *
     * @param  savedInstanceState the current state of the client
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        movieRating = 0f;
        lockedUsername="locked";
        lockedUser = new User(lockedUsername, "pineapples", "Rahul", "Raina");
        lockedUser.setLock(true);
        user_holder.put(lockedUsername, lockedUser);

    }

    /**
     * takes you to the filter screen
     * @param v
     */
    public void onClickFilter(View v) {
        if (movies_rated.size() == 0) {
            Toast.makeText(MainActivity.this, "No movies have been rated by users!",
                    Toast.LENGTH_SHORT).show();
        } else {
            setContentView(R.layout.filter_search);
        }
    }

    public void submitFilter(View v) {
        List<Movie> sorted = new ArrayList<Movie>(movies_rated);

        EditText filterMajor = (EditText) findViewById(R.id.filterMajor);
        CheckBox filterCheckBox= (CheckBox) findViewById(R.id.checkBoxRating);
        lv = (ListView) findViewById(R.id.movieslist);

        Comparator<Movie> comparator = null;
        String x = filterMajor.getText().toString();

        if (filterCheckBox.isChecked() && !filterMajor.getText().toString().equals("")) {
            List<Movie> other = new ArrayList<Movie>();
            for(Movie m: sorted) {
                if (m.ratedByMajor(filterMajor.getText().toString())) {
                    other.add(m);
                }
            }
            comparator = new RatingMajorComparator(filterMajor.getText().toString());
            sorted = other;
        } else if (filterCheckBox.isChecked()) {
            comparator = new RatingComparator();
        } else if (!filterMajor.getText().equals("")) {
            List<Movie> other = new ArrayList<Movie>();
            for(Movie m: sorted) {
                if (m.ratedByMajor(filterMajor.getText().toString())) {
                    other.add(m);
                }
            }
            comparator = null;
            sorted = other;
        } else {
            Toast.makeText(MainActivity.this, "Please select a filter", Toast.LENGTH_SHORT).show();
        }

        if (comparator != null) {
            Collections.sort(sorted, comparator);
        }


        List<String> a = new ArrayList<>();
        for(Movie e: sorted) {
            a.add(e.getTitle() + " - " + e.getAverageRating());
            System.out.println(e.getTitle());
        }


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                a);

        lv.setAdapter(arrayAdapter);


    }

    public void setUpAdminPage2(View v) {
        setUpAdminPage();
    }

    public void setUpAdminPage() {
        setContentView(R.layout.admin_page);
        ListView admin_user_list = (ListView) findViewById(R.id.adminUserList);

        List<String> usernames_for_admin = new ArrayList<>();

        for(User u: user_holder.values()) {
            usernames_for_admin.add(u.getFullName() + "         Banned: " + u.getBan() + " | Locked: " + u.getIsLocked());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                usernames_for_admin);

        admin_user_list.setAdapter(arrayAdapter);

    }

    public void toBanPage(View v) {
        setContentView(R.layout.ban_page);
    }

    class RatingComparator implements Comparator<Movie> {
        public int compare(Movie m1, Movie m2) {
            if(m2.getAverageRating() < m1.getAverageRating()) {
                return -1;
            } else if ( m2.getAverageRating() > m1.getAverageRating()) {
                return 1;
            }
            return 0;
        }
    }

    class RatingMajorComparator implements Comparator<Movie> {
        String major;
        public RatingMajorComparator(String major) {
            this.major = major;
        }
        public int compare(Movie m1, Movie m2) {
            if(m2.getRatingByMajor(major) < m1.getRatingByMajor(major)) {
                return -1;
            } else if ( m2.getRatingByMajor(major) > m1.getRatingByMajor(major)) {
                return 1;
            }
            return 0;
        }
    }

    /**
     * On click of the submit rating, store the rating and average it
     * @param v
     */
    public void submitRating(View v) {
        RatingBar movieRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        movieRating = movieRatingBar.getRating();

        String major = currentUser.getMajor();
        if (major.equals("")) {
            Toast.makeText(MainActivity.this, "You must specify your major before you may rate movies. Edit your profile to get started!",
                    Toast.LENGTH_SHORT).show();

        } else {
            currentMovie.addRating(major, movieRating);
            switchToMainApp();

            movietext = (SearchView) findViewById(R.id.searchView);
            movieTitle = (TextView) findViewById(R.id.titleofmovie);
            movieYear = (TextView) findViewById(R.id.yearofmovie);
            movieRated = (TextView) findViewById(R.id.ratingofmovie);
            movieUserRating = (TextView) findViewById(R.id.userratingofmovie);
            rateButton = findViewById(R.id.buttonrate);


            setMovieInformation();
        }


    }

    public void onClickSearchUserBase(View v) {
        SearchView adminSearchText = (SearchView) findViewById(R.id.adminSearchView);
        Button activationButton = (Button) findViewById(R.id.activatebutton);
        Button banningButton = (Button) findViewById(R.id.banbutton);
        Button unlockingButton = (Button) findViewById(R.id.unlockButton);


        String search_username = adminSearchText.getQuery().toString();
        TextView userToBan = (TextView) findViewById(R.id.nameofuser);
        for (User u:user_holder.values()) {
            if (search_username.equals(u.getUsername())) {
                userToBan.setText(u.getUsername());
                selectedUser = u;
                activationButton.setVisibility(View.VISIBLE);
                banningButton.setVisibility(View.VISIBLE);
                unlockingButton.setVisibility(View.VISIBLE);


            }
        }
    }

    public void activateUser(View v) {
        if (selectedUser != null) {
            if (!selectedUser.getBan()) {
                Toast.makeText(MainActivity.this, "This user is already active.",
                        Toast.LENGTH_SHORT).show();
            } else {
                selectedUser.setBan(false);
                Toast.makeText(MainActivity.this, selectedUser.getUsername() + " has been reactivated." , Toast.LENGTH_SHORT).show();

            }
        }
    }

    public void banUser(View v) {
        if (selectedUser != null) {
            if (selectedUser.getBan()) {
                Toast.makeText(MainActivity.this, "This user is already banned.",
                        Toast.LENGTH_SHORT).show();
            } else {
                selectedUser.setBan(true);
                Toast.makeText(MainActivity.this, selectedUser.getUsername() + " has been banned." , Toast.LENGTH_SHORT).show();

            }
        }
    }

    public void unlockUser(View v) {
        if (selectedUser != null) {
            if (!selectedUser.getIsLocked()) {
                Toast.makeText(MainActivity.this, "This user is already unlocked.",
                        Toast.LENGTH_SHORT).show();
            } else {
                selectedUser.setLock(false);
                Toast.makeText(MainActivity.this, selectedUser.getUsername() + " has been unlocked." , Toast.LENGTH_SHORT).show();

            }
        }
    }

    /**
     * search for the movie using the api for omdb and get the title, what its rated, and its year
     * @param v
     * @throws IOException
     */
    public void onClickSearch(View v) throws IOException {
        movietext = (SearchView) findViewById(R.id.searchView);
        movieTitle = (TextView) findViewById(R.id.titleofmovie);
        movieYear = (TextView) findViewById(R.id.yearofmovie);
        movieRated = (TextView) findViewById(R.id.ratingofmovie);
        movieUserRating = (TextView) findViewById(R.id.userratingofmovie);
        rateButton = findViewById(R.id.buttonrate);
        String rawMovieTitle = movietext.getQuery().toString();
        String trimmedTitle = rawMovieTitle.trim();
        String[] splitMovieTitle = rawMovieTitle.split(" ");
        String finishedTitle = "";
        for (int i = 0; i < splitMovieTitle.length; i++) {
            if (!splitMovieTitle[i].equals("")) {
                if (i == 0) {
                    finishedTitle = finishedTitle + splitMovieTitle[i];
                } else {
                    finishedTitle = finishedTitle + "+" + splitMovieTitle[i];
                }
            }
        }


//        Toast.makeText(MainActivity.this, "You have entered: " + movietext.getQuery(), Toast.LENGTH_SHORT).show();
        String urlString = "https://www.omdbapi.com/?t=" + finishedTitle + "&y=&plot=short&r=json";
        String resp = sendGetRequest(urlString);
//        Toast.makeText(MainActivity.this, resp, Toast.LENGTH_SHORT).show();
        try{
            jObject = new JSONObject(resp);
            String error = jObject.getString("Response");
            if(error.equals("False")) {
                movieTitle.setText(jObject.getString("Error"));
                movieYear.setText("");
                movieRated.setText("");
                movieUserRating.setText("");
                rateButton.setVisibility(View.GONE);
            } else {

                String year = jObject.getString("Year");
                String title = jObject.getString("Title");
                String rated = jObject.getString("Rated");
                String released = jObject.getString("Released");
                String runtime = jObject.getString("Runtime");
                currentMovie = new Movie(title, year, rated, released, runtime);
                if (!(movies_rated.contains(currentMovie))) {
                    movies_rated.add(currentMovie);
                } else {
                    currentMovie = movies_rated.get(movies_rated.indexOf(currentMovie));
                }
                setMovieInformation();
            }






//            Toast.makeText(MainActivity.this, "Year: " + year, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            System.out.print(e.getMessage());
        }
    }

    /**
     * Sets the actual information on the screen when the data gets pulled from the api
     */
    public void setMovieInformation() {

        movieTitle.setText(currentMovie.getTitle());
        movieYear.setText(currentMovie.getYear());
        movieRated.setText(currentMovie.getRated());

        if (!movies_rated.contains(currentMovie) || currentMovie.getAverageRating() == -1.0f) {
            movieUserRating.setText("No user rating");
        } else {
            movieUserRating.setText("Rating: " + String.valueOf(currentMovie.getAverageRating()));
        }
        rateButton.setVisibility(View.VISIBLE);
    }


    /**
     * when you click on the rate button, take you to the rating screen
     * @param v
     */
    public void onClickRate(View v) {
        setContentView(R.layout.rate_movie);
        movieTitleRate = (TextView) findViewById(R.id.movietitle);
        movieTitleRate.setText(currentMovie.getTitle());
        movieYearRate = (TextView) findViewById(R.id.movieyear);
        movieYearRate.setText(currentMovie.getYear());

    }

    /**
     * sends you back to the main screen once you're on the rate screen
     * @param v
     */
    public void onClickBackFromRate(View v) {
        switchToMainApp();
    }

    /**
     * conects to the api
     * @param urlString string represenation of the apiurl
     * @return returns a string of what the url returns
     * @throws IOException
     */
    public static String sendGetRequest(String urlString) throws IOException {
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

    /**
     * Transitions to the sign in screen view to allow
     * user to enter information there for login
     *
     * @param  v The view passed in which is used
     */
    public void onClickSignIn(View v) {
        setContentView(R.layout.sign_in_screen);
    }

    /**
     * Lets the user begin the process or registering with the system
     *
     * @param  v The view passed in that is used
     */
    public void onClickBeginRegistration(View v) {
        setContentView(R.layout.registration_screen);
        registerUsername = (EditText) findViewById(R.id.userRegisterUsername);
        registerPassword = (EditText) findViewById(R.id.userRegisterPassword);
        registerFirstName = (EditText) findViewById(R.id.userRegisterFirstName);
        registerLastName = (EditText) findViewById(R.id.userRegisterLastName);
    }



    /**
     * Allows user to register so the system can make a profile for them
     * We check for duplicate users in the map (O(1) lookup) and save
     * that information.
     *
     * @param  v The view passed in that is used
     */
    public void onClickRegister(View v) {
        Set usernames = user_holder.keySet();
        String username = registerUsername.getText().toString();
        String password = registerPassword.getText().toString();
        String firstname = registerFirstName.getText().toString();
        String lastname = registerLastName.getText().toString();

        if (username.equals("") || password.equals("")
                || firstname.equals("") || lastname.equals("") || username.equals(Admin.getUsername())) {
            Toast.makeText(MainActivity.this, "One or more fields is missing or invalid", Toast.LENGTH_SHORT).show();
        } else {
            boolean isUsernameTaken = usernames.contains(username);

            if (!isUsernameTaken) {
                //create user
                User newUser = new User(username, password,
                        firstname, lastname);
                user_holder.put(username, newUser);
                currentUser = newUser;
                registerUsername.setText("");
                registerLastName.setText("");
                registerFirstName.setText("");
                registerPassword.setText("");
                switchToMainApp();
            } else {
                Toast.makeText(MainActivity.this, "Username Taken", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Cancels registration and clears the input fields
     * if user cancels action.
     *
     * @param  v The view passed in that is used
     */
    public void onClickCancelRegister(View v) {
        registerUsername.setText("");
        registerLastName.setText("");
        registerFirstName.setText("");
        registerPassword.setText("");
        setContentView(R.layout.activity_main_screen);
    }




    /**
     * Obtains user's user and pass and checks some source
     * (could be db, hardcoded values, or a local map)
     * to log user in or alert incorrect login
     *
     * @param  v The view passed in that is used
     */
    public void onClickLogin(View v) throws IOException {
        username = (EditText) findViewById(R.id.editText);
        password = (EditText) findViewById(R.id.editText2);

        String usernameString = username.getText().toString();
        String passwordString = password.getText().toString();

        if (user_holder.keySet().contains(usernameString) && user_holder.get(usernameString).getBan()) {
            Toast.makeText(MainActivity.this, "This user is banned", Toast.LENGTH_SHORT).show();
        } else if (user_holder.keySet().contains(usernameString) && user_holder.get(usernameString).getIsLocked()) {
            Toast.makeText(MainActivity.this, "This user is locked", Toast.LENGTH_SHORT).show();
        } else {
            if (usernameString.equals(Admin.getUsername()) && passwordString.equals(Admin.getPassword())) {
                setUpAdminPage();
            } else {
                String urlstring = "http://rp-dev-env.szucsmaqnf.us-west-2.elasticbeanstalk.com/login.php?username="+usernameString + "&pass=" + passwordString;
                String resp = sendGetRequest(urlstring);
                try {
                    if (resp == null) {
                        password.setText("");
                        Toast.makeText(MainActivity.this, "Wrong username or password", Toast.LENGTH_SHORT).show();
                    } else {
                        jObject = new JSONObject(resp);
                        String statusmessage = jObject.getString("status_message");
                        JSONArray profileHolder = jObject.getJSONArray("profile");
                        currentUser = new User(usernameString, passwordString, profileHolder.getString(0), profileHolder.getString(1));
                        cameFromLogin = true;
                        switchToMainApp();
//
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                if (user_holder.keySet().contains(usernameString)
//                        && passwordString.equals(user_holder.get(usernameString).getPassword())) {
//                    setCurrentUser(usernameString);
//                    cameFromLogin = true;
//                    switchToMainApp();
//
//                } else {
//                    password.setText("");
//                    Toast.makeText(MainActivity.this, "Wrong username or password", Toast.LENGTH_SHORT).show();
//                }
            }


        }


    }

    /**
     * Allows user to sign back in after logout by taking him back
     * to the main screen.
     *
     * @param  v The view passed in that is used
     */
    public void onClickSignInBack(View v) {
        setContentView(R.layout.activity_main_screen);
    }

    /**
     * Signs user out and for now, simply empties the user and pass
     * fields and resets the view to the main screen.
     * Stability and usage of sessions are not currently
     * implemented.
     *
     * @param  v The view passed in that is used
     */
    public void onClickSignOut(View v) {
        if (cameFromLogin) {
            password.setText("");
        }
        setContentView(R.layout.activity_main_screen);
    }

    /**
     * Sets view to edit profile if user requests it.
     *
     * @param  v The view passed in that is used
     */
    public void onClickEditProfile(View v) {
        setContentView(R.layout.edit_profile);

        editUsername = (EditText) findViewById(R.id.userEditUsername);
        editPassword = (EditText) findViewById(R.id.userEditPassword);
        editFirstName = (EditText) findViewById(R.id.userEditFirstName);
        editLastName = (EditText) findViewById(R.id.userEditLastName);
        editMajor = (EditText) findViewById(R.id.userEditMajor);
        editInterests = (EditText) findViewById(R.id.userEditInterests);

        editUsername.setText(currentUser.getUsername());
        editPassword.setText(currentUser.getPassword());
        editFirstName.setText(currentUser.getFirstName());
        editLastName.setText(currentUser.getLastName());
        editMajor.setText(currentUser.getMajor());

        if (!currentUser.getInterests().equals("")) {
            editInterests.setText(currentUser.getInterests());
        }

    }

    /**
     * Placeholder which will eventually display the friends a user has
     *
     * @param  v The view passed in that is used
     */
    public void onClickFriends(View v) {
        //Temporary action
        Toast.makeText(MainActivity.this, "You don't have any friends yet!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Placeholder which will eventually display the groups which a user is in
     *
     * @param  v The view passed in that is used
     */
    public void onClickGroups(View v) {
        //Temporary action
        Toast.makeText(MainActivity.this, "You're not in any groups yet!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Attempts to update the fields of a profile, if valid
     * If updating username, checks to ensure no duplicate usernames
     * If there are no duplicates, it removes the old username
     * and reassigns the user to the newly entered username
     *
     * @param  v The view passed in that is used
     */
    public void onClickUpdateProfile(View v) {
        String newUsername = editUsername.getText().toString();
        String newPassword = editPassword.getText().toString();
        String newFirstName = editFirstName.getText().toString();
        String newLastName = editLastName.getText().toString();
        String newMajor = editMajor.getText().toString();
        String newInterests = editInterests.getText().toString();

        if (!(newUsername.equals(currentUser.getUsername()))
                || !(newPassword.equals(currentUser.getPassword()))
                || !(newFirstName.equals(currentUser.getFirstName()))
                || !(newLastName.equals(currentUser.getLastName()))
                || !(newMajor.equals(currentUser.getMajor()))
                || !(newInterests.equals(currentUser.getInterests()))) {

            if ((newUsername.equals(""))
                    || (newPassword.equals(""))
                    || (newFirstName.equals(""))
                    || (newLastName.equals(""))) {

                Toast.makeText(MainActivity.this, "Invalid field(s). Please check your input!", Toast.LENGTH_SHORT).show();
            } else {
                if (!(newUsername.equals(currentUser.getUsername()))) {
                    if (user_holder.keySet().contains(newUsername)) {
                        Toast.makeText(MainActivity.this, "Username taken", Toast.LENGTH_SHORT).show();
                    } else {
                        user_holder.remove(currentUser.getUsername());
                        currentUser.updateUser(newUsername, newPassword, newFirstName,
                                newLastName, newMajor, newInterests);
                        user_holder.put(currentUser.getUsername(), currentUser);
                    }
                } else {
                    currentUser.updateUser(newUsername, newPassword, newFirstName,
                            newLastName, newMajor, newInterests);
                }
            }
        } else {
            switchToMainApp();
        }

    }

    /**
     * Lets a user choose to cancel editing their profile
     *
     * @param  v The view passed in that is used
     */
    public void onClickCancelEdit(View v) {
        switchToMainApp();
    }


    /**
     * Helper method that allows us to set a currently logged in user
     * for information such as name, username, password, major, etc.
     *
     * @param  user The key to find the current user in the HashMap
     */
    public void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Helper method to switch to the core Rotten Potatoes opening screen
     *
     */
    public void switchToMainApp() {
        setContentView(R.layout.main_post_sign_in);
        profile_button = (TextView) findViewById(R.id.edit_profile);
        profile_button.setText(String.format("%s's Profile", currentUser.getFullName()));
    }







}