package com.example.rahulraina.rottenpotatoes;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;

import com.example.rahulraina.rottenpotatoes.R;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    // Variables for Registration Screen

    /**
     * EditText used in multiple places after Registration screen
     */
    private EditText registerUsername;

    /**
     * EditText used in multiple places after Registration screen
     */
    private EditText registerPassword;

    /**
     * EditText used in multiple places after Registration screen
     */
    private EditText registerFirstName;

    /**
     * EditText used in multiple places after Registration screen
     */
    private EditText registerLastName;

    /**
     * Lets us know if we have to clear the Login screen password upon sign out
     */
    private boolean cameFromLogin = false;


    /**
     * EditText used in multiple places after Edit Profile screen
     */
    private EditText editPassword;

    /**
     * EditText used in multiple places after Edit Profile screen
     */
    private EditText editFirstName;

    /**
     * EditText used in multiple places after Edit Profile screen
     */
    private EditText editLastName;

    /**
     * EditText used in multiple places after Edit Profile screen
     */
    private EditText editMajor;

    /**
     * EditText used in multiple places after Edit Profile screen
     */
    private EditText editInterests;


    /**
     * TextView used in multiple places after Rating screen
     */
    private TextView movieTitleRate;

    /**
     * TextView used in multiple places after Rating screen
     */
    private TextView movieYearRate;


    /**
     * Rating value used in multiple places after Rating screen
     */
    private Float movieRating;


    /**
     * User to be mannipulated by admin in multiple places after choosing screen
     */
    private User selectedUser;


    /**
     * The facebook login button and callbackManager
     *
     */
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private String full_name;




    /**
     * Starts the application and the cycle. First method to execute
     * so sets the view on start to the main screen
     *
     * @param  savedInstanceState the current state of the client
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main_screen);
        final int maxSDK  = 9;
        if (android.os.Build.VERSION.SDK_INT > maxSDK) {
            final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        movieRating = 0f;


        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));
        callbackManager = CallbackManager.Factory.create();


        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                setContentView(R.layout.main_post_sign_in);

                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                Profile profile = Profile.getCurrentProfile();
                                full_name = profile.getName();
                                String user_id = profile.getId();

                                User newUser = new FacebookUser(user_id, profile.getFirstName(), profile.getLastName());

                                RottenPotatoes.add(newUser);
                                RottenPotatoes.switchUser(newUser);
                                final TextView profileButton = (TextView) findViewById(R.id.edit_profile);
                                profileButton.setText(String.format("%s's Profile", RottenPotatoes.getCurrentUser().getFullName()));

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();


            }

            @Override
            public void onCancel() {
                // App code
                Log.v("LoginActivity", "cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.v("LoginActivity", exception.getCause().toString());
            }
        });


        // Adding the hardcoded locked user
        RottenPotatoes.addLockedUser();

        Toast.makeText(MainActivity.this, "This application is currently stripped of persistence",
                Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

//    public void loginfacebookuser(View v) {
//        AccessToken fbaccessToken = AccessToken.getCurrentAccessToken();
//        if (fbaccessToken != null) {
//            setContentView(R.layout.main_post_sign_in);
//        }
//    }

    /**
     * takes you to the filter screen
     * @param v the View to use
     */
    public void onClickFilter(View v) {
        if (!RottenPotatoes.moviesHaveBeenRated()) {
            Toast.makeText(MainActivity.this, "No movies have been rated by users!",
                    Toast.LENGTH_SHORT).show();
        } else {
            setContentView(R.layout.filter_search);
        }
    }

    /**
     * submits the filter the user would like to use
     * @param v the View to use
     */
    public void submitFilter(View v) {
        final List<Movie> moviesRated = RottenPotatoes.getRatedMovies();
        List<Movie> sorted = new ArrayList<>(moviesRated);

        final EditText filterMajor = (EditText) findViewById(R.id.filterMajor);
        final CheckBox filterCheckBox= (CheckBox) findViewById(R.id.checkBoxRating);
        final ListView lv = (ListView) findViewById(R.id.movieslist);

        sorted = RottenPotatoes.sortMovies(sorted, filterCheckBox.isChecked(),
                filterMajor.getText().toString());

        if (sorted == null) {
            Toast.makeText(MainActivity.this, "Please select a filter and try again", Toast.LENGTH_SHORT).show();
        }

        final List<String> a = new ArrayList<>();
        for(final Movie e: sorted) {
            a.add(e.getTitle() + " - " + e.getAverageRating());
//            System.out.println(e.getTitle());
        }


        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                a);

        lv.setAdapter(arrayAdapter);
    }

    /**
     * Sets up the Admin homepage
     */
    public void setUpAdminPage() {
        setContentView(R.layout.admin_page);
        final ListView adminUserList = (ListView) findViewById(R.id.adminUserList);

        final List<String> adminUsernames = RottenPotatoes.getAdminUsernameList();

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                adminUsernames);

        adminUserList.setAdapter(arrayAdapter);

    }

    /**
     * Sets up the page for admin to ban users
     * @param v the View to use
     */
    public void toBanPage(View v) {
        setContentView(R.layout.ban_page);
    }


    /**
     * Coordinates the search of users by an admin
     * @param v the view to be used
     */
    public void onClickSearchUserBase(View v) {
        final SearchView adminSearchText = (SearchView) findViewById(R.id.adminSearchView);
        final Button activationButton = (Button) findViewById(R.id.activatebutton);
        final  Button banningButton = (Button) findViewById(R.id.banbutton);
        final Button unlockingButton = (Button) findViewById(R.id.unlockButton);


        final String searchUsername = adminSearchText.getQuery().toString();
        final TextView userToBan = (TextView) findViewById(R.id.nameofuser);
        final User user = RottenPotatoes.adminUserSearch(searchUsername);
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

    /**
     * Un-bans a user upon the Admin's selection
     * @param v the View to be used
     */
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

    /**
     * Helper method for the admin to ban a user
     * @param v the Veiw to be used
     */
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

    /**
     * Helper method for the admin to unlock a locked user
     * @param v the View to be used
     */
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
     * @param v the View to be used
     * @throws IOException if searchOMDB fails
     */
    public void onClickSearch(View v) throws IOException {
        final SearchView movieText = (SearchView) findViewById(R.id.searchView);

        final String rawMovieTitle = movieText.getQuery().toString();
        final String[] splitMovieTitle = rawMovieTitle.trim().split(" ");
        String finishedTitle = "";

        for (int i = 0; i < splitMovieTitle.length; i++) {
            if (!"".equals(splitMovieTitle[i])) {
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
     * @param v the view to be used
     */
    public void submitRating(View v) {
        final RatingBar movieRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        movieRating = movieRatingBar.getRating();

        final String major = RottenPotatoes.getCurrentUser().getMajor();

        if ("".equals(major)) {
            Toast.makeText(MainActivity.this, "You must specify your major before you may rate movies. Edit your profile to get started!",
                    Toast.LENGTH_SHORT).show();

        } else {
            final Movie currentMovie = RottenPotatoes.getCurrentMovie();
            currentMovie.addRating(major, movieRating);
            switchToMainApp();
            setMovieInformation(currentMovie);
        }
    }

    /**
     * Sets the actual information on the screen when the data gets pulled from the api
     * @param currentMovie the movie we are setting the info for
     */
    public void setMovieInformation(Movie currentMovie) {

        final TextView movieTitle = (TextView) findViewById(R.id.titleofmovie);
        final TextView movieYear = (TextView) findViewById(R.id.yearofmovie);
        final TextView movieRated = (TextView) findViewById(R.id.ratingofmovie);
        final TextView movieUserRating = (TextView) findViewById(R.id.userratingofmovie);
        final View rateButton = findViewById(R.id.buttonrate);


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
                movieUserRating.setText("Rating: " + currentMovie.getAverageRating());
            }
            rateButton.setVisibility(View.VISIBLE);
            RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
            ratingBar.setVisibility(View.VISIBLE);
        }
    }


    /**
     * when you click on the rate button, take you to the rating screen
     * @param v the View to be used
     */
    public void onClickRate(View v) {
        if (!"".equals(RottenPotatoes.getCurrentUser().getMajor())) {
            final Movie currentMovie = RottenPotatoes.getCurrentMovie();
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
     * @param v the View to be used
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
     * @throws IOException if the registration fails
     */
    public void onClickRegister(View v) throws IOException {
        final String username = registerUsername.getText().toString();
        final String password = registerPassword.getText().toString();
        final String firstName = registerFirstName.getText().toString();
        final String lastName = registerLastName.getText().toString();

        final User user = RottenPotatoes.registerUser(username, password, firstName, lastName);

        if (user == null) {
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
     * @throws IOException if the online login fails
     */
    public void onClickLogin(View v) throws IOException {
        final EditText username = (EditText) findViewById(R.id.editText);
        final EditText password = (EditText) findViewById(R.id.editText2);

        final String usernameString = username.getText().toString();
        final String passwordString = password.getText().toString();

        final User blankUser = new User("", "", "", "");

        final User user = RottenPotatoes.localLogin(usernameString, passwordString);
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
        final EditText password = (EditText) findViewById(R.id.editText2);
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

        final User currentUser = RottenPotatoes.getCurrentUser();
        editPassword.setText(currentUser.getPassword());
        editFirstName.setText(currentUser.getFirstName());
        editLastName.setText(currentUser.getLastName());
        editMajor.setText(currentUser.getMajor());

        if (!"".equals(currentUser.getInterests())) {
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
     * @throws IOException if the online login fails
     */
    public void onClickUpdateProfile(View v) throws IOException {
        final String newPassword = editPassword.getText().toString();
        final String newFirstName = editFirstName.getText().toString();
        final String newLastName = editLastName.getText().toString();
        final String newMajor = editMajor.getText().toString();
        final String newInterests = editInterests.getText().toString();

//        final String[] splitMajor = newMajor.trim().split(" ");
//        String finishedMajor = "";
//        for (int i = 0; i < splitMajor.length; i++) {
//            if (!]"".equals(splitMajor[i)) {
//                if (i == 0) {
//                    finishedMajor = finishedMajor + splitMajor[i];
//                } else {
//                    finishedMajor  = finishedMajor + "+" + splitMajor[i];
//                }
//            }
//        }

        final String display = RottenPotatoes.updateProfile(newPassword, newFirstName, newLastName,
                newMajor, newInterests);
        Toast.makeText(MainActivity.this, display, Toast.LENGTH_SHORT).show();


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
        final TextView profileButton = (TextView) findViewById(R.id.edit_profile);
        profileButton.setText(String.format("%s's Profile", RottenPotatoes.getCurrentUser().getFullName()));
    }
}