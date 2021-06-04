package com.rodnog.rogermiddenway.foodrescue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.rodnog.rogermiddenway.foodrescue.data.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {
    DatabaseHelper db;
    boolean anim1HasRun;
    boolean anim2HasRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EditText usernameEditText = findViewById(R.id.usernameEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.logInButton);
        Button signUpButton = findViewById(R.id.signUpButton);

        SharedPreferences prefs = getSharedPreferences("SHAREPREFS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();;

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
                    startActivity(mainIntent);
                    finish();
                    overridePendingTransition(R.anim.enter_up, R.anim.exit_up);

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
                overridePendingTransition(R.anim.enter_right_to_left, R.anim.exit_right_to_left);

            }
        });

        anim1HasRun = false;
        anim2HasRun = false;
        ImageView helicopter = findViewById(R.id.lHelicopterImageView);
        Path path1 = new Path();
        path1.arcTo(-500, 0, 1500, 500, 0, 180, true);
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(helicopter, View.X, View.Y, path1);
        anim1.setInterpolator(new LinearInterpolator());
        anim1.setDuration(2000);

        Path path2 = new Path();
        path2.moveTo(400, -300);

        path2.lineTo(400, 200);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(helicopter, View.X, View.Y, path2);
        anim2.setInterpolator(new LinearInterpolator());
        anim2.setDuration(3000);


        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!anim1HasRun){
                    anim1.start();
                    anim1HasRun = true;
                }
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!anim2HasRun){
                    anim2.start();
                    anim2HasRun = true;
                }
            }
        });

    }
}