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
    // Variables for Login Screen
    private EditText username;
    private EditText password;
    private TextView profile_button;

    // Variables for Registration Screen
    private EditText registerUsername;
    private EditText registerPassword;
    private EditText registerFirstName;
    private EditText registerLastName;

    // HashMap from username to User Object
    HashMap<String, User> user_holder = new HashMap<>();

    private User currentUser;

    // Lets us know if we have to clear the Login screen password
    private boolean cameFromLogin = false;

    // Variables for Edit Profile Screen
    private EditText editUsername;
    private EditText editPassword;
    private EditText editFirstName;
    private EditText editLastName;
    private EditText editMajor;
    private EditText editInterests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
    }


///////////////////////////////////////////////////////

    // These are the methods for the buttons on the opening Register/Sign In screen
    public void onClickSignIn(View v) {
        setContentView(R.layout.sign_in_screen);
    }

    public void onClickBeginRegistration(View v) {
        setContentView(R.layout.registration_screen);
        registerUsername = (EditText) findViewById(R.id.userRegisterUsername);
        registerPassword = (EditText) findViewById(R.id.userRegisterPassword);
        registerFirstName = (EditText) findViewById(R.id.userRegisterFirstName);
        registerLastName = (EditText) findViewById(R.id.userRegisterLastName);
    }

///////////////////////////////////////////////////////

    // These are the methods for the buttons on the Registration screen
    public void onClickRegister(View v) {
        Set usernames = user_holder.keySet();
        String username = registerUsername.getText().toString();
        String password = registerPassword.getText().toString();
        String firstname = registerFirstName.getText().toString();
        String lastname = registerLastName.getText().toString();

        if (username.equals("") || password.equals("")
                || firstname.equals("") || lastname.equals("")) {
            Toast.makeText(MainActivity.this, "One or more fields is missing or invalid", Toast.LENGTH_SHORT).show();
        } else {
            boolean isUsernameTaken = usernames.contains(username);

            if (!isUsernameTaken) {
                //create user
                User newUser = new User(username, password,
                        firstname, lastname);
                user_holder.put(username, newUser);
                setCurrentUser(username);
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

    public void onClickCancelRegister(View v) {
        registerUsername.setText("");
        registerLastName.setText("");
        registerFirstName.setText("");
        registerPassword.setText("");
        setContentView(R.layout.activity_main_screen);
    }

///////////////////////////////////////////////////////

    // These are the methods for the buttons on the Login screen
    public void onClickLogin(View v) {
        username = (EditText) findViewById(R.id.editText);
        password = (EditText) findViewById(R.id.editText2);

        String usernameString = username.getText().toString();
        String passwordString = password.getText().toString();

        if (user_holder.keySet().contains(usernameString)
                && passwordString.equals(user_holder.get(usernameString).getPassword())) {
            setCurrentUser(usernameString);
            cameFromLogin = true;
            switchToMainApp();

        } else {
            password.setText("");
            Toast.makeText(MainActivity.this, "Wrong username or password", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickSignInBack(View v) {
        setContentView(R.layout.activity_main_screen);
    }

///////////////////////////////////////////////////////

    // These are the methods for the main app opening screen
    public void onClickSignOut(View v) {
        if (cameFromLogin) {
            password.setText("");
        }
        setContentView(R.layout.activity_main_screen);
    }

    public void onClickEditProfile(View v) {
//        editUsername = (EditText) findViewById(R.id.userEditUsername);
//        editPassword = (EditText) findViewById(R.id.userEditPassword);
//        editFirstName = (EditText) findViewById(R.id.userEditFirstName);
//        editLastName = (EditText) findViewById(R.id.userEditLastName);
//        editMajor = (EditText) findViewById(R.id.userEditMajor);
//        editInterests = (EditText) findViewById(R.id.userEditInterests);
//
//        editUsername.setText(currentUser.getUsername());
//        editPassword.setText(currentUser.getPassword());
//        editFirstName.setText(currentUser.getFirstName());
//        editLastName.setText(currentUser.getLastName());
//        editMajor.setText(currentUser.getMajor());
//
//        if (currentUser.getInterests().equals("")) {
//            editInterests.setText("Enter your interests here!");
//        } else {
//            editInterests.setText(currentUser.getInterests());
//        }

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

        if (currentUser.getInterests().equals("")) {
            editInterests.setText("Enter your interests here!");
        } else {
            editInterests.setText(currentUser.getInterests());
        }

    }

    public void onClickFriends(View v) {
        //Temporary action
        Toast.makeText(MainActivity.this, "You don't have any friends yet!", Toast.LENGTH_SHORT).show();
    }

    public void onClickGroups(View v) {
        //Temporary action
        Toast.makeText(MainActivity.this, "You're not in any groups yet!", Toast.LENGTH_SHORT).show();
    }

///////////////////////////////////////////////////////

    // These methods are for the buttons on the update profile screen
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

    public void onClickCancelEdit(View v) {
        switchToMainApp();
    }



///////////////////////////////////////////////////////

    // Helper Methods

    // This sets/updates the current user variables

    public void setCurrentUser(String username) {
        currentUser = user_holder.get(username);
    }

    // This switches to the main application opening screen
    // Used by the Login screen and Registration screen

    public void switchToMainApp() {
        setContentView(R.layout.main_post_sign_in);
        profile_button = (TextView) findViewById(R.id.edit_profile);
        profile_button.setText(String.format("%s's Profile", currentUser.getFullName()));
    }


}