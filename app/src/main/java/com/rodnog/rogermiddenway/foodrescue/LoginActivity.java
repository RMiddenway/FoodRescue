package com.rodnog.rogermiddenway.foodrescue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rodnog.rogermiddenway.foodrescue.data.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EditText usernameEditText = findViewById(R.id.usernameEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.logInButton);
        Button signUpButton = findViewById(R.id.signUpButton);

        db = new DatabaseHelper(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int user_id = db.fetchUser(usernameEditText.getText().toString(), passwordEditText.getText().toString());
                if(user_id >= 0){
                    Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt("CURRENT_USER", user_id);
                    editor.apply();
                    int yep = sharedPref.getInt("CURRENT_USER", -1);
//                    Toast.makeText(LoginActivity.this, "User id:" + String.valueOf(user_id) + " yes " + String.valueOf(yep), Toast.LENGTH_SHORT).show();
                    startActivity(mainIntent);
                    finish();
                }
                else{
                    Toast.makeText(LoginActivity.this, "User doesn't exist", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent signupIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(signupIntent);
            }
        });
    }
}