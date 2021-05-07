package com.rodnog.rogermiddenway.foodrescue;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rodnog.rogermiddenway.foodrescue.data.DatabaseHelper;
import com.rodnog.rogermiddenway.foodrescue.model.User;

public class SignUpActivity extends AppCompatActivity {
    DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        EditText sNameEditText = findViewById(R.id.sNameEditText);
        EditText sEmail = findViewById(R.id.sEmailEditText);
        EditText sPhone = findViewById(R.id.sPhoneEditText);
        EditText sAddress = findViewById(R.id.sAddressEditText);
        EditText sPasswordEditText = findViewById(R.id.sPasswordEditText);
        EditText sConfirmPasswordEditText = findViewById(R.id.sConfirmPasswordEditText);
        Button sSignUpButton = findViewById(R.id.sSignUpButton);

        db = new DatabaseHelper(this);

        sSignUpButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String name = sNameEditText.getText().toString();
                String email = sEmail.getText().toString();
                String phone = sPhone.getText().toString();
                String address = sAddress.getText().toString();
                String password = sPasswordEditText.getText().toString();
                String confirmPassword = sConfirmPasswordEditText.getText().toString();

                if(password.equals(confirmPassword)){
                    long result = db.insertUser(new User(name, email, phone, address, password));
                    if(result > 0){
                        Toast.makeText(SignUpActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else{
                        Toast.makeText(SignUpActivity.this, "Registration error", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(SignUpActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}