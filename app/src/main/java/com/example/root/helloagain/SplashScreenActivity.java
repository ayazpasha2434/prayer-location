package com.example.root.helloagain;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;

/**
 * Created by root on 16/7/15.
 */
public class SplashScreenActivity extends Activity {

    private static final long SPLASH_SCREEN_DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);

                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN_DELAY);
    }
}
