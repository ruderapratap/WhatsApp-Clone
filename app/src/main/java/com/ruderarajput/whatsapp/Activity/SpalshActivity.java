package com.ruderarajput.whatsapp.Activity;
import static com.ruderarajput.whatsapp.utils.SharedPreferenceUtils.isLoggedIn;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.ruderarajput.whatsapp.R;
import com.ruderarajput.whatsapp.utils.SharedPreferenceUtils;

public class SpalshActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 2000; // Splash screen delay in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);

        getSupportActionBar().hide();

        // Delayed navigation based on login status
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateToNextScreen();
            }
        }, SPLASH_DELAY);
    }


    private boolean isLoggedIn() {
        return SharedPreferenceUtils.isLoggedIn(this);
    }
    private void navigateToNextScreen() {
        if (isLoggedIn()) {
            navigateToMainActivity();
        } else {
            navigateToLoginActivity();
        }
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(SpalshActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToLoginActivity() {
        Intent intent = new Intent(SpalshActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

}
