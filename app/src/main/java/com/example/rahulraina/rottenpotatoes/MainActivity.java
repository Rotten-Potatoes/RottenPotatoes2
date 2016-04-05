package com.example.rahulraina.rottenpotatoes;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rahulraina.rottenpotatoes.R;


import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

    // Lets us know if we have to clear the Login screen password upon sign out
    private boolean cameFromLogin = false;

    // Variables for Edit Profile Screen
    //private EditText editUsername;
    private EditText editPassword;
    private EditText editFirstName;
    private EditText editLastName;
    private EditText editMajor;
    private EditText editInterests;

    private TextView movieTitle;
    private TextView movieYear;
    private TextView movieRated;
    private TextView movieUserRating;

    private TextView movieTitleRate;
    private TextView movieYearRate;

    private View rateButton;
    private Float movieRating;

    private SearchView movietext;
    User selectedUser;




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

        // Adding the hardcoded locked user
        RottenPotatoes.add(RottenPotatoes.lockedUser);

        Toast.makeText(MainActivity.this, "This application is currently stripped of persistence",
                Toast.LENGTH_SHORT).show();

    }

    /**
     * takes you to the filter screen
     * @param v
     */
    public void onClickFilter(View v) {
        if (!RottenPotatoes.moviesHaveBeenRated()) {
            Toast.makeText(MainActivity.this, "No movies have been rated by users!",
                    Toast.LENGTH_SHORT).show();
        } else {
            setContentView(R.layout.filter_search);
        }
    }

    public void submitFilter(View v) {
        List<Movie> movies_rated = RottenPotatoes.getRatedMovies();
        List<Movie> sorted = new ArrayList<>(movies_rated);

        EditText filterMajor = (EditText) findViewById(R.id.filterMajor);
        CheckBox filterCheckBox= (CheckBox) findViewById(R.id.checkBoxRating);
        lv = (ListView) findViewById(R.id.movieslist);

        Comparator<Movie> comparator = null;

        sorted = RottenPotatoes.sortMovies(sorted, filterCheckBox.isChecked(),
                filterMajor.getText().toString());

        if (sorted == null) {
            Toast.makeText(MainActivity.this, "Please select a filter and try again", Toast.LENGTH_SHORT).show();
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

    public void setUpAdminPage() {
        setContentView(R.layout.admin_page);
        ListView admin_user_list = (ListView) findViewById(R.id.adminUserList);

        List<String> adminUsernames = RottenPotatoes.getAdminUsernameList();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                adminUsernames);

        admin_user_list.setAdapter(arrayAdapter);

    }

    public void toBanPage(View v) {
        setContentView(R.layout.ban_page);
    }


    public void onClickSearchUserBase(View v) {
        SearchView adminSearchText = (SearchView) findViewById(R.id.adminSearchView);
        Button activationButton = (Button) findViewById(R.id.activatebutton);
        Button banningButton = (Button) findViewById(R.id.banbutton);
        Button unlockingButton = (Button) findViewById(R.id.unlockButton);


        String search_username = adminSearchText.getQuery().toString();
        TextView userToBan = (TextView) findViewById(R.id.nameofuser);
        User user = RottenPotatoes.adminUserSearch(search_username);
        if (user == null) {
            Toast.makeText(MainActivity.this, "No such user found",
                    Toast.LENGTH_SHORT).show();
        } else {
            userToBan.setText(user.getUsername());
            selectedUser = user;
            activationButton.setVisibility(View.VISIBLE);
            banningButton.setVisibility(View.VISIBLE);
            unlockingButton.setVisibility(View.VISIBLE);
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

        String rawMovieTitle = movietext.getQuery().toString();
        String[] splitMovieTitle = rawMovieTitle.trim().split(" ");
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

        setMovieInformation(RottenPotatoes.searchOMDB(finishedTitle));

    }


    /**
     * On click of the submit rating, store the rating and average it
     * @param v
     */
    public void submitRating(View v) {
        RatingBar movieRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        movieRating = movieRatingBar.getRating();

        String major = RottenPotatoes.getCurrentUser().getMajor();

        if (major.equals("")) {
            Toast.makeText(MainActivity.this, "You must specify your major before you may rate movies. Edit your profile to get started!",
                    Toast.LENGTH_SHORT).show();

        } else {
            Movie currentMovie = RottenPotatoes.getCurrentMovie();
            currentMovie.addRating(major, movieRating);
            switchToMainApp();
            setMovieInformation(currentMovie);
        }
    }

    /**
     * Sets the actual information on the screen when the data gets pulled from the api
     */
    public void setMovieInformation(Movie currentMovie) {

        movieTitle = (TextView) findViewById(R.id.titleofmovie);
        movieYear = (TextView) findViewById(R.id.yearofmovie);
        movieRated = (TextView) findViewById(R.id.ratingofmovie);
        movieUserRating = (TextView) findViewById(R.id.userratingofmovie);
        rateButton = findViewById(R.id.buttonrate);


        if (currentMovie == null) {
            movieTitle.setText("There was an OMDb Error");
            movieYear.setText("");
            movieRated.setText("");
            movieUserRating.setText("");
            rateButton.setVisibility(View.GONE);
        } else {
            movieTitle.setText(currentMovie.getTitle());
            movieYear.setText(currentMovie.getYear());
            movieRated.setText(currentMovie.getRated());

            if (!RottenPotatoes.isRated(currentMovie)) {
                movieUserRating.setText("No user rating");
            } else {
                movieUserRating.setText("Rating: " + String.valueOf(currentMovie.getAverageRating()));
            }
            rateButton.setVisibility(View.VISIBLE);
        }
    }


    /**
     * when you click on the rate button, take you to the rating screen
     * @param v
     */
    public void onClickRate(View v) {
        if (!RottenPotatoes.getCurrentUser().getMajor().equals("")) {
            Movie currentMovie = RottenPotatoes.getCurrentMovie();
            setContentView(R.layout.rate_movie);
            movieTitleRate = (TextView) findViewById(R.id.movietitle);
            movieTitleRate.setText(currentMovie.getTitle());
            movieYearRate = (TextView) findViewById(R.id.movieyear);
            movieYearRate.setText(currentMovie.getYear());
        } else {
            Toast.makeText(MainActivity.this, "You must specify your major before you may rate movies. Edit your profile to get started!",
                    Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * sends you back to the main screen once you're on the rate screen
     * @param v
     */
    public void onClickBackFromRate(View v) {
        switchToMainApp();
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
    public void onClickRegister(View v) throws IOException {
//        Set usernames = user_holder.keySet();
        String username = registerUsername.getText().toString();
        String password = registerPassword.getText().toString();
        String firstName = registerFirstName.getText().toString();
        String lastName = registerLastName.getText().toString();

        User user = RottenPotatoes.registerUser(username, password, firstName, lastName);

        if(user == null) {
            Toast.makeText(MainActivity.this, "One or more fields is missing or invalid", Toast.LENGTH_SHORT).show();
        } else {
                RottenPotatoes.switchUser(user);
                registerUsername.setText("");
                registerLastName.setText("");
                registerFirstName.setText("");
                registerPassword.setText("");
                switchToMainApp();
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

        User blankUser = new User("", "", "", "");

        User user = RottenPotatoes.localLogin(usernameString, passwordString);
        if (user != null) {
            if (user.getBan()) {
                Toast.makeText(MainActivity.this, "This user is banned", Toast.LENGTH_SHORT).show();
            } else if (user.getIsLocked()) {
                Toast.makeText(MainActivity.this, "This user is locked", Toast.LENGTH_SHORT).show();
            } else if (Admin.isAdmin(user)) {
                setUpAdminPage();
            } else if (user.equals(blankUser)) {
                password.setText("");
                Toast.makeText(MainActivity.this, "Wrong username or password", Toast.LENGTH_SHORT).show();
            } else {
                RottenPotatoes.switchUser(user);
                cameFromLogin = true;
                switchToMainApp();
            }
        } else {
            /** Temporarily remove persistence code **/
            /*
            user = RottenPotatoes.onlineLogin(usernameString, passwordString);
            */
            // We will be adding more checks for locks and bans once persistence for them has been added
            if (user == null) {
                password.setText("");
                Toast.makeText(MainActivity.this, "Wrong username or password", Toast.LENGTH_SHORT).show();
            } else {
                RottenPotatoes.switchUser(user);
                cameFromLogin = true;
                switchToMainApp();
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

        editPassword = (EditText) findViewById(R.id.userEditPassword);
        editFirstName = (EditText) findViewById(R.id.userEditFirstName);
        editLastName = (EditText) findViewById(R.id.userEditLastName);
        editMajor = (EditText) findViewById(R.id.userEditMajor);
        editInterests = (EditText) findViewById(R.id.userEditInterests);

        User currentUser = RottenPotatoes.getCurrentUser();
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
    public void onClickUpdateProfile(View v) throws IOException {
        String newPassword = editPassword.getText().toString();
        String newFirstName = editFirstName.getText().toString();
        String newLastName = editLastName.getText().toString();
        String newMajor = editMajor.getText().toString();
        String newInterests = editInterests.getText().toString();

        String[] splitMajor = newMajor.trim().split(" ");
        String finishedMajor = "";
        for (int i = 0; i < splitMajor.length; i++) {
            if (!splitMajor[i].equals("")) {
                if (i == 0) {
                    finishedMajor = finishedMajor + splitMajor[i];
                } else {
                    finishedMajor  = finishedMajor + "+" + splitMajor[i];
                }
            }
        }

        switch(RottenPotatoes.updateProfile(newPassword,
                newFirstName, newLastName,
                newMajor, newInterests,
                finishedMajor)) {
            case 0:
                Toast.makeText(MainActivity.this, "Invalid field(s). Please check your input!", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(MainActivity.this, "Your profile was updated successfully!", Toast.LENGTH_SHORT).show();
                break;
            default:
        }

        switchToMainApp();

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
     * Helper method to switch to the core Rotten Potatoes opening screen
     *
     */
    public void switchToMainApp() {
        setContentView(R.layout.main_post_sign_in);
        profile_button = (TextView) findViewById(R.id.edit_profile);
        profile_button.setText(String.format("%s's Profile", RottenPotatoes.getCurrentUser().getFullName()));
    }
}