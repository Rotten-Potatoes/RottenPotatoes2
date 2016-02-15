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

    //Variables for Registration Screen
    private EditText registerUsername;
    private EditText registerPassword;
    private EditText registerFirstName;
    private EditText registerLastName;

    //HashMap indexed by username
    HashMap<String, User> user_holder = new HashMap<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
    }

    public void onClickSignIn(View v) {
        setContentView(R.layout.sign_in_screen);

    }

    public void onClickLogin(View v) {
        username = (EditText) findViewById(R.id.editText);
        password = (EditText) findViewById(R.id.editText2);

        if (username.getText().toString().toLowerCase().equals("user")
                && password.getText().toString().equals("pass")) {
            setContentView(R.layout.content_main);
        } else {
            password.setText("");
            Toast.makeText(MainActivity.this, "Wrong username or password", Toast.LENGTH_SHORT).show();
        }


    }

    public void onClickRegister(View v) {
        registerUsername = (EditText) findViewById(R.id.userRegisterUsername);
        registerPassword = (EditText) findViewById(R.id.userRegisterPassword);
        registerFirstName = (EditText) findViewById(R.id.userRegisterFirstName);
        registerLastName = (EditText) findViewById(R.id.userRegisterLastName);

        Set usernames = user_holder.keySet();
        boolean isUsernameTaken = usernames.contains(registerUsername.getText().toString());

        if (usernames.size() == 0 || !isUsernameTaken) {
            //create user
            User newUser = new User(registerUsername.getText().toString(),
                    registerPassword.getText().toString(), registerFirstName.getText().toString(),
                    registerLastName.getText().toString());
            setContentView(R.layout.content_main);
        } else {
            Toast.makeText(MainActivity.this, "Username Taken", Toast.LENGTH_SHORT).show();
        }




    }

    public void sign_in_back_click(View v) {
        setContentView(R.layout.activity_main_screen);
    }

    public void onClickSignOut(View v) {
        username.setText("");
        password.setText("");
        setContentView(R.layout.activity_main_screen);
    }

    public void onClickCancelRegister(View v) {
        registerUsername.setText("");
        registerLastName.setText("");
        registerFirstName.setText("");
        registerPassword.setText("");

        setContentView(R.layout.activity_main_screen);
    }
}