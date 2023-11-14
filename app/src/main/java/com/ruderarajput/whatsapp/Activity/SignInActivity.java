package com.ruderarajput.whatsapp.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.ruderarajput.whatsapp.databinding.ActivitySignInBinding;

public class SignInActivity extends AppCompatActivity {

    ActivitySignInBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();


        binding.continueBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.etMobile.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(SignInActivity.this, "Please Enter Your Mobile Number.", Toast.LENGTH_SHORT).show();
                    binding.etMobile.requestFocus();
                }
                if (binding.etEmail.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(SignInActivity.this, "Please Enter Your Email Address.", Toast.LENGTH_SHORT).show();
                    binding.etEmail.requestFocus();
                } else if (!binding.etEmail.getText().toString().trim().matches(emailPattern)) {
                    Toast.makeText(SignInActivity.this, "Please Enter Correct Email Address.", Toast.LENGTH_SHORT).show();
                    binding.etEmail.requestFocus();
                } else if (binding.etPassword.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(SignInActivity.this, "Please Enter Your Email Address.", Toast.LENGTH_SHORT).show();
                    binding.etPassword.requestFocus();
                } else if (binding.etPassword.getText().toString().trim().length() < 6) {
                    Toast.makeText(SignInActivity.this, "Please Enter Minimum 6 Digit Password.", Toast.LENGTH_SHORT).show();
                    binding.etPassword.requestFocus();
                } else if (!binding.etConfirmPassword.getText().toString().trim().equals(binding.etPassword.getText().toString().trim())) {
                    Toast.makeText(SignInActivity.this, "Confirm Password Doesn't Match.", Toast.LENGTH_SHORT).show();
                    binding.etPassword.requestFocus();
                } else {
                    Intent intent = new Intent(SignInActivity.this, OtpActivity.class);
                    intent.putExtra("phoneNumber", binding.etMobile.getText().toString());
                    startActivity(intent);
                }
            }
        });
        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}


