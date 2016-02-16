package com.example.rahulraina.rottenpotatoes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rahulraina.rottenpotatoes.R;

import java.util.HashMap;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    //Variables for Login Screen
    private EditText username;
    private EditText password;
    private TextView profile_button;

    //Variables for Registration Screen
    private EditText registerUsername;
    private EditText registerPassword;
    private EditText registerFirstName;
    private EditText registerLastName;

    //HashMap indexed by username
    HashMap<String, User> user_holder = new HashMap<>();


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
     * Obtains user's user and pass and checks some source
     * (could be db, hardcoded values, or a local map)
     * to log user in or alert incorrect login
     *
     * @param  v The view passed in that is used
     */
    public void onClickLogin(View v) {
        username = (EditText) findViewById(R.id.editText);
        password = (EditText) findViewById(R.id.editText2);

        if (username.getText().toString().toLowerCase().equals("user")
                && password.getText().toString().equals("pass")) {
            setContentView(R.layout.main_post_sign_in);
            profile_button = (TextView) findViewById(R.id.edit_profile);
            String name =  "User Name";
            profile_button.setText(String.format("%s's Profile", name));
        } else {
            password.setText("");
            Toast.makeText(MainActivity.this, "Wrong username or password", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Allows user to register so the system can make a profile for them
     * We check for duplicate users in the map (O(1) lookup) and save
     * that information.
     *
     * @param  v The view passed in that is used
     */
    public void onClickRegister(View v) {
        Toast.makeText(MainActivity.this, "Well it got here", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.registration_screen);
        registerUsername = (EditText) findViewById(R.id.userRegisterUsername);
        registerPassword = (EditText) findViewById(R.id.userRegisterPassword);
        registerFirstName = (EditText) findViewById(R.id.userRegisterFirstName);
        registerLastName = (EditText) findViewById(R.id.userRegisterLastName);

        Set usernames = user_holder.keySet();
        boolean isUsernameTaken = usernames.contains(registerUsername.getText().toString());

//        if (usernames.size() == 0 || !isUsernameTaken) {
//            //create user
//            User newUser = new User(registerUsername.getText().toString(),
//                    registerPassword.getText().toString(), registerFirstName.getText().toString(),
//                    registerLastName.getText().toString());
//            setContentView(R.layout.main_post_sign_in);
//        } else {
//            Toast.makeText(MainActivity.this, "Username Taken", Toast.LENGTH_SHORT).show();
//        }
    }
    /**
     * Allows user to sign back in after logout by taking him back
     * to the main screen.
     *
     * @param  v The view passed in that is used
     */
    public void sign_in_back_click(View v) {
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
        username.setText("");
        password.setText("");
        setContentView(R.layout.activity_main_screen);
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
     * Sets view to edit profile if user requests it.
     *
     * @param  v The view passed in that is used
     */
    public void onClickProfile(View v) {
        setContentView(R.layout.edit_profile);
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
}