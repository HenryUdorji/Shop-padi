package com.codemountain.shoppadiclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.codemountain.shoppadiclient.R;

public class SplashScreenActivity extends AppCompatActivity {
    private static final String TAG = "SplashScreenActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Thread splash = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                    startActivity(new Intent(SplashScreenActivity.this, HomeActivity.class));
                    finish();
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                }catch (Exception e){
                    Log.d(TAG, "run: " + e.getMessage());
                }
            }
        };
        splash.start();
    }
}
