package com.example.rahulraina.rottenpotatoes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rahulraina.rottenpotatoes.R;

public class MainActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;


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

    public void sign_in_back_click(View v) {
        setContentView(R.layout.activity_main_screen);
    }

    public void onClickSignOut(View v) {
        username.setText("");
        password.setText("");
        setContentView(R.layout.activity_main_screen);
    }
}